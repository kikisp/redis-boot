package redis.spike.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.spike.demo.model.User;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

@Component
public class AppListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        /*String key = "user_key";
        Queue<String> array = new LinkedList<>();
        redisTemplate.opsForValue().set(key,new User("TestName","TestPass",new Date(),array));

*/
    }

}
