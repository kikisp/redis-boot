package redis.spike.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import redis.spike.demo.model.AuthUser;
import redis.spike.demo.model.RoleUsername;
import redis.spike.demo.model.User;
import redis.spike.demo.repository.UserDetailRepository;

import java.util.Optional;
import redis.spike.demo.repository.UserRepository;

@Service("userDetailsService")
public class UserDetailServiceImplementation implements UserDetailsService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        Optional<User> optionalUser = userDetailRepository.findByUsername(name);

        optionalUser.orElseThrow(() -> new UsernameNotFoundException("Username or password wrong"));

        UserDetails userDetails = new AuthUser(optionalUser.get());
        new AccountStatusUserDetailsChecker().check(userDetails);
        return userDetails;
    }


    public List<RoleUsername> getAllUsers() {
        return userRepository.getAllUsers();
    }
}
