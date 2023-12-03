package dk.kea.mulpenbackend.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.kea.mulpenbackend.JwtTokenManager;
import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.dto.JwtRequestModel;
import dk.kea.mulpenbackend.dto.JwtResponseModel;
import dk.kea.mulpenbackend.model.UserModel;
import dk.kea.mulpenbackend.service.JwtUserDetailsService;
import dk.kea.mulpenbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;



@RestController
public class UserController {
    private final ConfigProvider configProvider;

    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenManager jwtTokenManager;
    @Autowired
    private UserService userService;

    @Autowired
    public UserController(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }



    @PostMapping("/login")
    public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception {
        System.out.println(" JwtController createToken Call: 4" + request.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(),
                            request.getPassword())
            );
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponseModel("Bad credentials"));
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwtToken = jwtTokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken));
    }

    private static Set<String> invalidatedTokens = new HashSet<>();

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authToken = request.getHeader("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);

            // Perform token invalidation logic
            if (!invalidatedTokens.contains(authToken)) {
                invalidatedTokens.add(authToken);
                System.out.println("Invalidating token: " + authToken);
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has already been invalidated");
            }
        }

        return ResponseEntity.badRequest().body("Invalid token format");
    }


    @PostMapping("/getSecret")
    public ResponseEntity<Map> getSecret() {
        System.out.println("getSecret is called");
        Map<String, String> map = new HashMap<>();
        map.put("message", "this is secret from server");
        return ResponseEntity.ok(map);
    }

    @GetMapping("/allUsers")
    public List<UserModel> getAllUsers() {
        return userService.getAllUsers();
    }

    private String[] allowedExtensions = {
            "jpg", "png", "jpeg", "gif", "webp"
    };
    @PostMapping(value = "/addUser", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addUser(@RequestPart("file") MultipartFile file, @RequestPart("user") String userJson) throws JsonProcessingException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            // Deserialize userJson to UserModel
            ObjectMapper objectMapper = new ObjectMapper();
            UserModel userModel = objectMapper.readValue(userJson, UserModel.class);

            String safeFileName = FilenameUtils.getName(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(safeFileName);

            if (extension == null || !Arrays.asList(allowedExtensions).contains(extension)) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }


            Path uploadPath = Paths.get(configProvider.profileDirectory, safeFileName);
            userModel.setFilePath(safeFileName);

            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

            userService.save(userModel);

            return ResponseEntity.ok().body("{\"message\": \"File upload successful: " + safeFileName + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
        }
    }

}
