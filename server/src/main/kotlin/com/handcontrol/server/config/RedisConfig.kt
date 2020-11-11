package com.handcontrol.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@Configuration
class RedisConfig(val properties: RedisProperties) {
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val config = RedisStandaloneConfiguration(properties.host, properties.port.toInt())
        val jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().build()
        val factory = JedisConnectionFactory(config, jedisClientConfiguration)
        factory.afterPropertiesSet()
        return factory
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template: RedisTemplate<String, Any> = RedisTemplate()
        template.connectionFactory = JedisConnectionFactory()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    @Bean
    fun redisContainer(): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.connectionFactory = jedisConnectionFactory()
        return container
    }
}
