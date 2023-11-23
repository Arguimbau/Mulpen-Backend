package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.model.MediaItem;
import dk.kea.mulpenbackend.service.MediaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@CrossOrigin
@RestController
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping("/media")
    public void showMedia(HttpServletResponse response) throws IOException{
        List<MediaItem> mediaList = mediaService.getAllMedia();

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head>");
        htmlBuilder.append("<meta charset=\"UTF-8\"/>");
        htmlBuilder.append("<title>Media Gallery</title>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<h2>Media Gallery</h2>");

        for (MediaItem mediaItem : mediaList) {
            htmlBuilder.append("<div>");
            htmlBuilder.append("<h3>").append(mediaItem.getDescription()).append("</h3>");


            if ("image".equals(mediaItem.getType())) {
                htmlBuilder.append("<img src=\"").append(mediaItem.getFilePath()).append("\" alt=\"Image\"/>");
            }

            if ("video".equals(mediaItem.getType())){
                htmlBuilder.append("<video width=\"320\" height=\"240\" controls>");
                htmlBuilder.append("<source src=\"").append(mediaItem.getFilePath()).append("\" type=\"video/mp4\"/>");
                htmlBuilder.append("Your browser does not support the video tag.");
                htmlBuilder.append("</video>");
            }

            htmlBuilder.append("<p>").append(mediaItem.getDescription()).append("</p>");
            htmlBuilder.append("</div>");
        }

        htmlBuilder.append("</body>");
        htmlBuilder.append("</html>");

        response.setContentType("text/html");
        response.getWriter().write(htmlBuilder.toString());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("description") String description) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {

            MediaItem mediaItem = new MediaItem();



            String uploadDirectory = System.getenv("MEDIA_FILE_PATH");

            Path uploadPath = Paths.get(uploadDirectory, file.getOriginalFilename());

            //Set Image path and description
            mediaItem.setFilePath(uploadDirectory);
            mediaItem.setDescription(description);

            //File type handling

            String fileType = file.getContentType();
            System.out.println(fileType);

            mediaItem.setType(fileType);


            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);


            mediaService.saveMedia(mediaItem);

            return ResponseEntity.ok("File upload successful:" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload");
        }
    }
}
