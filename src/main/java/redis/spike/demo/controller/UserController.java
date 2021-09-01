package redis.spike.demo.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.spike.demo.model.AuthUser;
import redis.spike.demo.model.RoleUsername;
import redis.spike.demo.service.UserDetailServiceImplementation;

@RestController
public class UserController {

    @Autowired
    UserDetailServiceImplementation userService;

    @GetMapping(name = "/user/me", produces= MediaType.APPLICATION_JSON_VALUE)
    public RoleUsername   getCurrentUser( ) throws JSONException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();
        UserDetails details = userService.loadUserByUsername(username);
        RoleUsername roleUsername = new RoleUsername(details.getUsername(), "USER");
        System.out.println("role and username " +roleUsername);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Content-Type", "application/json;charset=UTF-8");
        ResponseEntity re = new ResponseEntity(roleUsername, headers, HttpStatus.OK);
        //return  re;
        JSONObject json = new JSONObject();
        json.put("username", details.getUsername());
        json.put("role", "USER");
        return roleUsername;
    }


}
