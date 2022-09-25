package io.seqera.wave.service.token

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.lettuce.core.api.StatefulRedisConnection
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.seqera.wave.configuration.TokenConfig
import io.seqera.wave.service.ContainerRequestData
import jakarta.inject.Singleton

/**
 * Implements container request token store based on a Redis cache
 *
 * @author : jorge <jorge.aguilera@seqera.io>
 *
 */
@Requires(property = 'redis.uri')
@Replaces(LocalTokenStore)
@Singleton
@CompileStatic
@Slf4j
class RedisTokenStore implements ContainerTokenStore {

    ObjectMapper mapper = new ObjectMapper()

    StatefulRedisConnection<String,String> redisConnection

    TokenConfig tokenConfiguration

    RedisTokenStore(
            TokenConfig config,
            StatefulRedisConnection<String,String> redisConnection) {
        this.tokenConfiguration = config
        this.redisConnection = redisConnection
        log.info "Redis tokens store - duration=$config.cache.duration; maxSize=$config.cache.maxSize"
    }

    private String key(String name) { "wave-tokens:$name" }

    @Override
    ContainerRequestData put(String name, ContainerRequestData request) {
        def json = new JsonBuilder(request).toString()
        // once created the token the user has `Duration` time to pull the layers of the image
        redisConnection.sync().psetex(key(name), tokenConfiguration.cache.duration.toMillis(), json)
        return request
    }

    @Override
    ContainerRequestData get(String name) {
        def json = redisConnection.sync().get(key(name))
        if( !json )
            return null
        def requestData = mapper.readValue(json, ContainerRequestData)
        return requestData
    }
}
