package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.model.MediaItem;
import dk.kea.mulpenbackend.service.MediaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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


@RequestMapping("/media")
@CrossOrigin
@RestController
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @GetMapping
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


            if (mediaItem.getType().contains("image")) {
                htmlBuilder.append("<img src=\"").append(mediaItem.getFilePath()).append("\" alt=\"Image\"/>");
            }

            if (mediaItem.getType().contains("video")){
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


    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws IOException {
        Resource resource = mediaService.loadMediaAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    //@CrossOrigin("*")
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("description") String description) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "http://localhost:63342");

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {

            MediaItem mediaItem = new MediaItem();



            String uploadDirectory = System.getenv("MEDIA_FILE_PATH");

            Path uploadPath = Paths.get(uploadDirectory, file.getOriginalFilename());

            //Set Image path and description
            mediaItem.setFilePath("media/"+file.getOriginalFilename());
            mediaItem.setDescription(description);

            //File type handling

            String fileType = file.getContentType();
            System.out.println(fileType);

            mediaItem.setType(fileType);


            Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);


            mediaService.saveMedia(mediaItem);

            return ResponseEntity.ok().headers(headers).body("File upload successful:" + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload");
        }
    }
}
