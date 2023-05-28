package com.rohailiqbal.user.config;

import com.rohailiqbal.user.domain.UserDomain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration class for Redis.
 */
@Configuration
public class RedisConfiguration {

    /**
     * Returns a RedisTemplate bean for Redis operations with UserDomain as value type.
     *
     * @param redisConnectionFactory the Redis connection factory
     * @return the RedisTemplate bean
     */
    @Bean
    public RedisTemplate<String, UserDomain> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserDomain> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserDomain.class));
        return template;
    }

    /**
     * Returns a ValueOperations bean for Redis operations with UserDomain as value type.
     *
     * @param redisTemplate the RedisTemplate bean
     * @return the ValueOperations bean
     */
    @Bean
    public ValueOperations<String, UserDomain> valueOperations(RedisTemplate<String, UserDomain> redisTemplate) {
        return redisTemplate.opsForValue();
    }
}