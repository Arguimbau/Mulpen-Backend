package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.model.MediaAttachment;
import dk.kea.mulpenbackend.model.MediaPost;
import dk.kea.mulpenbackend.repository.MediaAttachmentRepository;
import dk.kea.mulpenbackend.repository.MediaPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
public class PostController {
    @Autowired
    private MediaPostRepository mediaPostRepository;

    @Autowired
    private MediaAttachmentRepository mediaAttachmentRepository;

    @PostMapping("/create-post")
    public String createPost(@RequestParam("text") String text,
                             @RequestParam("file")MultipartFile file) throws IOException {

        MediaPost mediaPost = new MediaPost();
        mediaPost.setText(text);
        mediaPostRepository.save(mediaPost);

        if (file != null && !file.isEmpty()){
            MediaAttachment attachment = new MediaAttachment();
            attachment.setData(file.getBytes());
            attachment.setMediaPost(mediaPost);
            mediaAttachmentRepository.save(attachment);
        }

        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public ResponseEntity<List<MediaPost>> getAllPosts(){
        List<MediaPost> posts = mediaPostRepository.findAll();
        return ResponseEntity.ok(posts);
    }
}
