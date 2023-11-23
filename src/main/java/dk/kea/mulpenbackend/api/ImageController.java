package dk.kea.mulpenbackend.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
@Controller
public class ImageController {
    @GetMapping("/media/download.png")
    @ResponseBody
    public ResponseEntity<Resource> getImage(){
        try {
            Resource resource = new ClassPathResource("media/download.png");
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}

 */