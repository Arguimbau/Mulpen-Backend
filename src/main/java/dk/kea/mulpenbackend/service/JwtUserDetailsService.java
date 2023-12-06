package dk.kea.mulpenbackend.service;

import dk.kea.mulpenbackend.model.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private IUserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(" JwtUserDetailsService loadUserByUsername Call: 5,6");
        List<UserModel> users = userService.findByName(username);
        System.out.println("users from database: length: " + users.size());
        if(users.size()==1) {
            System.out.println("found the user in Database: " + users.get(0).getUsername());
                return new User(username,
                        users.get(0).getPassword(),
                        new ArrayList<>());
        }else{
                throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
