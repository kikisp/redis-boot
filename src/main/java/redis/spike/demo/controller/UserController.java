package redis.spike.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.spike.demo.model.AuthUser;
import redis.spike.demo.model.RoleUsername;
import redis.spike.demo.service.UserDetailServiceImplementation;

import java.util.HashSet;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserDetailServiceImplementation userService;


    @GetMapping(value="/user/me",produces = MediaType.APPLICATION_JSON_VALUE)
        public RoleUsername getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        UserDetails details = userService.loadUserByUsername(username);
        HashSet<GrantedAuthority> set = (HashSet<GrantedAuthority>) details.getAuthorities();
        String role = set.stream().findFirst().get().getAuthority();
        return new RoleUsername(details.getUsername(),role);
    }
}
