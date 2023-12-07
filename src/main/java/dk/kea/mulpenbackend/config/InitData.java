package dk.kea.mulpenbackend.config;

import dk.kea.mulpenbackend.model.UserModel;
import dk.kea.mulpenbackend.repository.UserRepository;
import dk.kea.mulpenbackend.service.UserService;
import dk.kea.mulpenbackend.service.MediaService;
import dk.kea.mulpenbackend.service.SlideshowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    MediaService mediaService;

    @Autowired
    SlideshowService slideshowService;

    @Override
    public void run(String... args) throws Exception {
        UserModel user = new UserModel();
        user.setPassword("1234");
        user.setUsername("admin");
        userService.save(user);

        mediaService.addExistingMedia();
        slideshowService.addExistingSlideshow();

        UserModel user2 = new UserModel();
        user2.setPassword("1234");
        user2.setUsername("user1");
        user2.setName("Amalie Vixø Drøscher");
        user2.setPhoneNumber("+45 2064 9953");
        user2.setTitle("Administrerende direktør, Tilrættelægger, fotograf og klipper");
        user2.setEmail("amalie@mulpen.dk");
        user2.setFilePath("images/profile1.jpg");
        userService.save(user2);

        UserModel user3 = new UserModel();
        user3.setPassword("1234");
        user3.setUsername("user2");
        user3.setName("Konrad Jon");
        user3.setPhoneNumber("+45 3131 9391");
        user3.setTitle("Underdirektør, Producer og økonomiansvarlig");
        user3.setEmail("konrad@mulpen.dk");
        user3.setFilePath("images/profile2.jpg");
        userService.save(user3);

        UserModel user4 = new UserModel();
        user4.setPassword("1234");
        user4.setUsername("user3");
        user4.setName("Emil Holmsteen");
        user4.setPhoneNumber("+45 7170 0678");
        user4.setTitle("Co-founder Skuespiller og idéudvikler");
        user4.setEmail("emil@mulpen.dk");
        user4.setFilePath("images/profile3.jpg");
        userService.save(user4);

        UserModel user5 = new UserModel();
        user5.setPassword("1234");
        user5.setUsername("user4");
        user5.setName("Gustav Halfdan");
        user5.setPhoneNumber("+45 2032 1659");
        user5.setTitle("Co-founder Komiker og idéudvikler");
        user5.setEmail("gustav@mulpen.dk");
        user5.setFilePath("images/profile4.jpg");
        userService.save(user5);
    }
}