package redis.spike.demo.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.spike.demo.model.RoleUsername;
import redis.spike.demo.model.User;
import redis.spike.demo.service.UserDetailServiceImplementation;

import java.util.HashSet;

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
        String role = set.stream()
            .filter(a->(a.getAuthority().equals("ROLE_ADMIN")||a.getAuthority().equals("ROLE_USER") ))
            .findFirst().get().getAuthority();
        return new RoleUsername(details.getUsername(),role);
    }

    @GetMapping(value="/users")
    public List<RoleUsername> getAllUSers() {

        List<RoleUsername> users= userService.getAllUsers();
        System.out.println(users);
        return users;

    }
}
