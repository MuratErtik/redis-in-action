package org.murat.redisinaction.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String messageBody = new String(message.getBody());
        String matchedPattern = new String(pattern);

        System.out.println("The Channel: " + channel + " -> The Message: " + messageBody);
        System.out.println("The Pattern: " + matchedPattern);

    }


}


