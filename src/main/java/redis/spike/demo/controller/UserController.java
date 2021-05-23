package redis.spike.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.spike.demo.model.AuthUser;
import redis.spike.demo.service.UserDetailServiceImplementation;

@RestController
public class UserController {

    @Autowired
    UserDetailServiceImplementation userService;

    @GetMapping("/user/me")
    public String getCurrentUser( ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        UserDetails details = userService.loadUserByUsername(username);
        return details.getUsername();
    }
}
