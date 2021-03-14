package redis.spike.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.spike.demo.config.CustomRedisTokenStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

@RestController
public class RedisController {

    @Autowired
    private CustomRedisTokenStore tokenStore;

    /*@Autowired
    RedisTemplate redisTemplate;

    @GetMapping("/redis")
    public String getValue(@RequestParam String key) {
        User user =(User)redisTemplate.opsForValue().get(key);
        return user.getName();
    }

    @GetMapping("/redis/array")
    public Collection<String> getArray(@RequestParam String key) {
        User user =(User)redisTemplate.opsForValue().get(key);
        return user.getArray();
    }

    @PostMapping("/redis")
    public String setValue() {
        String key = "user_key";
        User user =(User)redisTemplate.opsForValue().get(key);
        Queue<String> array = Optional.ofNullable(user.getArray()).orElse(new LinkedList<>());
        array.add("aa");
        redisTemplate.opsForValue().set(key,new User("TestName","TestPass",new Date(),array));
        return key;
    }*/


    @GetMapping("/redis/remove")
    public void removeToken(@RequestParam String username) {

        tokenStore.getTokensByUsername(username);

    }

    @GetMapping("/private")
    public String test() {

        return "hello";
    }
}