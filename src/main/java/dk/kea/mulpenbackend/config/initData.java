package dk.kea.mulpenbackend.config;

import dk.kea.mulpenbackend.Entity.User;
import dk.kea.mulpenbackend.repository.UserRepository;
import dk.kea.mulpenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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
        userService.save(user);
    }
}