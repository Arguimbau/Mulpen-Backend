package dk.kea.mulpenbackend.config;

import dk.kea.mulpenbackend.model.UserModel;
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
        UserModel user = new UserModel();
        user.setPassword("1234");
        user.setUsername("admin");
        userService.save(user);

        UserModel user2 = new UserModel();
        user2.setPassword("1234");
        user2.setUsername("user");
        userService.save(user2);
    }
}