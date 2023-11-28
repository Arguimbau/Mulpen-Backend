package dk.kea.mulpenbackend.config;

import dk.kea.mulpenbackend.Entity.User;
import dk.kea.mulpenbackend.Repository.UserRepository;
import dk.kea.mulpenbackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class InitData implements CommandLineRunner
{

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Override
    public void run(String... args) throws Exception
    {
        User user = new User();
        user.setPassword("1234");
        user.setUsername("admin");
        user.setLocalTime(LocalTime.now());
        userService.save(user);
    }
}