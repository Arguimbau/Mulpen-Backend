package dk.kea.mulpenbackend.api;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class viewController {

    @GetMapping("/videos")
    public void forwardToViewVideoPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/viewVideo.html");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/upload")
    public void forwardToUploadMediaPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/uploadMedia.html");
        requestDispatcher.forward(request, response);
    }
}
