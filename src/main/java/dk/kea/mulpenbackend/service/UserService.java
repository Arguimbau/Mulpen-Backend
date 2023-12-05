package dk.kea.mulpenbackend.service;


import dk.kea.mulpenbackend.config.SecurityConfiguration;
import dk.kea.mulpenbackend.model.UserModel;
import dk.kea.mulpenbackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserService implements IUserService{

    private UserRepository userRepository;
    @Override
    public Set<UserModel> findAll() {
        Set<UserModel> set = new HashSet<>();
        userRepository.findAll().forEach(set::add);
        return set;
    }

    public UserModel save(UserModel user) {
        System.out.println("User before encoding password: " + user.toString());

        if (user.getPassword() == null) {
            // Handle the case where the password is null, you might throw an exception or handle it as appropriate for your application.
        }

        PasswordEncoder pw = SecurityConfiguration.passwordEncoder();
        user.setPassword(pw.encode(user.getPassword()));
        user.setUsername(user.getUsername());
        user.setName(user.getName());
        user.setEmail(user.getEmail());
        user.setTitle(user.getTitle());
        return userRepository.save(user);
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void delete(UserModel user) {
        try {
            userRepository.delete(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete user");

        }
    }



    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public Optional<UserModel> findById(Long aLong) {
        return userRepository.findById(aLong);
    }

    @Override
    public List<UserModel> findByName(String name) {
        System.out.println("Userservice called findByName with argument: " + name);
        return userRepository.findByUsername(name);
    }
    public ResponseEntity<String> addExistingMedia() {
        try {
            String mediaDirectoryPath = System.getenv("USER_FILE_PATH");

            List<Path> mediaFiles = Files.walk(Paths.get(mediaDirectoryPath), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for (Path mediaFilePath : mediaFiles) {

                String fileName = mediaFilePath.getFileName().toString();

                if (!userRepository.existsByFilePath("user/" + fileName)) {
                    UserModel user = new UserModel();
                    user.setFilePath(fileName);
                    user.setTitle("Description for " + fileName);

                    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();

                    user.setType(fileExtension);

                    save(user);
                }
            }
            return ResponseEntity.ok("Existing user files added to database.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred while adding existing user.");
        }
    }
}
