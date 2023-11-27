package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.config.ConfigProvider;
import dk.kea.mulpenbackend.model.MediaModel;
import dk.kea.mulpenbackend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/media")
@CrossOrigin
@RestController
public class MediaController {
  private final ConfigProvider configProvider;

  @Autowired
  private MediaService mediaService;


  @Autowired
  public MediaController(ConfigProvider configProvider) {
    this.configProvider = configProvider;
  }

    /*
    @GetMapping
    public void showMedia(HttpServletResponse response) throws IOException{
        List<MediaModel> mediaList = mediaService.getAllMedia();

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>");
        htmlBuilder.append("<html>");
        htmlBuilder.append("<head><link rel=\"stylesheet\" type=\"text/css\" href=\"https:/Arguimbau.github.io/Mulpen-Frontend/tree/main/css/media.css\"></head>");
        htmlBuilder.append("<meta charset=\"UTF-8\"/>");
        htmlBuilder.append("<title>Media Gallery</title>");
        htmlBuilder.append("</head>");
        htmlBuilder.append("<body>");
        htmlBuilder.append("<h2>Media Gallery</h2>");

        for (MediaModel mediaItem : mediaList) {
            htmlBuilder.append("<div>");
            htmlBuilder.append("<h3>").append(HtmlUtils.htmlEscape(mediaItem.getDescription())).append("</h3>");


            if (mediaItem.getType().startsWith("image/")) {
                htmlBuilder.append("<img src=\"").append(HtmlUtils.htmlEscape(mediaItem.getFilePath())).append("\" alt=\"Image\"/>");
            }

            if (mediaItem.getType().startsWith("video/")){
                htmlBuilder.append("<video width=\"320\" height=\"240\" controls src=\"" + HtmlUtils.htmlEscape(mediaItem.getFilePath()) +"\">");
//                htmlBuilder.append("<source src=\"").append(HtmlUtils.htmlEscape(mediaItem.getFilePath())).append("\" type=\"video/mp4\"/>");
                htmlBuilder.append("Your browser does not support the video tag.");
                htmlBuilder.append("</video>");
            }

            if (mediaItem.getType().startsWith("audio/")) {
                htmlBuilder.append("<audio controls>");
                htmlBuilder.append("<source src=\"").append(HtmlUtils.htmlEscape(mediaItem.getFilePath())).append("\" type=\"audio/mpeg\"/>");
                htmlBuilder.append("</audio>");
            }

            htmlBuilder.append("<p>").append(HtmlUtils.htmlEscape(mediaItem.getDescription())).append("</p>");
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

        String[] filenameParts = fileName.split("\\."); // a.b.c -> [a, b, c]
        String ext = filenameParts[filenameParts.length - 1].toLowerCase();

        if (ext.endsWith(".jpg")) { //content sniffing
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
                    .body(resource);
        } else if (ext.endsWith(".png")) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "image/png")
                    .body(resource);
        } else if (ext.endsWith(".mp4")){
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .body(resource);
        } else if (ext.endsWith(".mp3")) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .body(resource);
        }
        throw new UnexpectedFileTypeException("The given file type is not supported.");
    }

    //@CrossOrigin("*")
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("description") String description) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {

            MediaModel mediaItem = new MediaModel();

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

            return ResponseEntity.ok("File upload successful: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
        }
    }

     */

  @GetMapping("/upload/{filename:.+}")
  public ResponseEntity<Resource> getFile(@PathVariable String filename) {
    String safeFileName = FilenameUtils.getName(filename);
    Path filePath = Paths.get(configProvider.uploadDirectory, safeFileName);
    Resource resource = new org.springframework.core.io.PathResource(filePath);

    try {
      // Set content-type dynamically based on the file type
      String contentType = Files.probeContentType(filePath);

      return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        //.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }
  }

  private String[] badExtensions = {"java", "htm", "html"};
  private String[] allowedExtensions = {
    "jpg", "png", "jpeg", "gif",
    "mp4", "mov", "mkv", "avi", "mp3", "wav", "flac", "webm", "webp"
  };

  @PostMapping("/upload")
  public ResponseEntity<String> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("description") String description) {

    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please select a file to upload");
    }


    try {

      MediaModel mediaModel = new MediaModel();
      String safeFileName = FilenameUtils.getName(file.getOriginalFilename());
      String extension = FilenameUtils.getExtension(safeFileName);
      if (extension == null) {
        return ResponseEntity.badRequest().body("File type not allowed");
      }

      extension = extension.toLowerCase();
      if (Arrays.asList(badExtensions).contains(extension)) {
        return ResponseEntity.badRequest().body("File type not allowed");
      }
      if (!Arrays.asList(allowedExtensions).contains(extension)) {
        return ResponseEntity.badRequest().body("File type not allowed");
      }

      Path uploadPath = Paths.get(configProvider.uploadDirectory, safeFileName);

      mediaModel.setFilePath(safeFileName);
      mediaModel.setDescription(description);

      Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

      mediaService.saveMedia(mediaModel);

      return ResponseEntity.ok("File upload successful: " + safeFileName);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body("Error occurred during file upload: " + file.getOriginalFilename());
    }
  }

  @GetMapping("/all")
  public List<MediaModel> getAllMedia() {
    return mediaService.getAllMedia();
  }
}

