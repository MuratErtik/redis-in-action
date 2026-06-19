package org.murat.redisinaction.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;


    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        System.out.println("The message sent: " + message);
    }
}
