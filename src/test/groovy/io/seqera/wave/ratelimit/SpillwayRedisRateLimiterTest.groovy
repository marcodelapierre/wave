package io.seqera.wave.ratelimit


import spock.lang.Specification

import io.micronaut.context.ApplicationContext
import io.seqera.wave.exception.SlowDownException
import io.seqera.wave.configuration.RateLimiterConfig
import io.seqera.wave.ratelimit.impl.SpillwayRateLimiter
import io.seqera.wave.test.RedisTestContainer
import redis.clients.jedis.JedisPool

/**
 * @author : jorge <jorge.aguilera@seqera.io>
 *
 */
class SpillwayRedisRateLimiterTest extends Specification implements RedisTestContainer {

    ApplicationContext applicationContext

    SpillwayRateLimiter rateLimiter

    JedisPool jedisPool

    def setup() {
        applicationContext = ApplicationContext.run([
                REDIS_HOST   : redisHostName,
                REDIS_PORT   : redisPort
        ], 'test', 'redis','rate-limit')
        rateLimiter = applicationContext.getBean(SpillwayRateLimiter)
        jedisPool = new JedisPool(redisHostName, redisPort as int)
        jedisPool.resource.flushAll()
    }

    void "can acquire 1 auth resource"() {
        when:
        rateLimiter.acquireBuild(new AcquireRequest("test", null))
        then:
        noExceptionThrown()
        and:
        jedisPool.resource.scan("0").result.size() == 1
        jedisPool.resource.scan("0").result.first().startsWith('spillway|authenticatedBuilds|perUser|test')
    }

    void "can acquire 1 anon resource"() {
        when:
        rateLimiter.acquireBuild(new AcquireRequest(null, "test"))
        then:
        noExceptionThrown()
        and:
        jedisPool.resource.scan("0").result.size() == 1
        jedisPool.resource.scan("0").result.first().startsWith('spillway|anonymousBuilds|perUser|test')
    }

    void "can't acquire more resources"() {
        given:
        RateLimiterConfig config = applicationContext.getBean(RateLimiterConfig)

        when:
        (0..config.build.authenticated.max - 1).each {
            rateLimiter.acquireBuild( new AcquireRequest("test", null))
        }
        then:
        noExceptionThrown()
        and:
        jedisPool.resource.scan("0").result.size() == 1

        when:
        rateLimiter.acquireBuild( new AcquireRequest("test", null))

        then:
        thrown(SlowDownException)
    }

}
