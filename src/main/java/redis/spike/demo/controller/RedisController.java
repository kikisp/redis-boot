package redis.spike.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {


    @GetMapping("/public")
    public String testPublic() {

        return "THIS IS FREE";

    }

    @GetMapping("/private")
    public String testPrivate() {

        return "THIS IS PRIVATE";
    }
}