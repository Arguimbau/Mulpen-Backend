package dk.kea.mulpenbackend.config;

import dk.kea.mulpenbackend.model.UserModel;
import dk.kea.mulpenbackend.repository.UserRepository;
import dk.kea.mulpenbackend.service.MediaService;
import dk.kea.mulpenbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InitData implements CommandLineRunner
{
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    MediaService mediaService;

    @Override
    public void run(String... args) throws Exception
    {
        UserModel user = new UserModel();
        user.setPassword("1234");
        user.setUsername("admin");
        user.setRoles(Set.of("USER", "ADMIN"));




        userService.save(user);

        mediaService.addExistingMedia();

        System.out.println(user.getRoles());
    }
}