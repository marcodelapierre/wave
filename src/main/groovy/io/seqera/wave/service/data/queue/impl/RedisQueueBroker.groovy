package io.seqera.wave.service.data.queue.impl


import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Requires
import io.seqera.wave.service.data.queue.MessageBroker
import jakarta.inject.Inject
import jakarta.inject.Singleton
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
/**
 * Implements a message broker using Redis list
 *
 * @author Paolo Di Tommaso <paolo.ditommaso@gmail.com>
 */
@Slf4j
@Requires(env = 'redis')
@Singleton
@CompileStatic
class RedisQueueBroker implements MessageBroker<String>  {

    @Inject
    private JedisPool pool

    @Override
    void offer(String target, String message) {
        try (Jedis conn = pool.getResource()) {
            conn.lpush(target, message)
        }
    }

    @Override
    String take(String target) {
        try (Jedis conn = pool.getResource()) {
            return conn.rpop(target)
        }
    }

    @Override
    boolean hasMark(String prefix) {
        try (Jedis conn = pool.getResource()) {
            return conn.keys(prefix + '*')?.size()>0
        }
    }

    @Override
    void mark(String key) {
        try (Jedis conn = pool.getResource()) {
            conn.set(key, 'true')
        }
    }

    @Override
    void unmark(String key) {
        try (Jedis conn = pool.getResource()) {
            conn.del(key)
        }
    }

}
