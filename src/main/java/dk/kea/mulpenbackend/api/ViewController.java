package dk.kea.mulpenbackend.api;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

@Controller
@EnableWebMvc
public class ViewController {

    /// PRODUCTION //////
    @PreAuthorize("permitAll()")
    @GetMapping("/videos")
    public void forwardToViewVideoPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/viewVideo.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/viewMedia")
    public void forwardToViewMediaPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      ServletContext context = request.getServletContext();
      RequestDispatcher requestDispatcher = context.getRequestDispatcher("/viewMedia.html");
      requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/upload")
    public void forwardToUploadMediaPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/uploadMedia.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/deleteMedia")
    public void forwardToDeleteMediaPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/delete-media.html");
        requestDispatcher.forward(request, response);
    }



    @PreAuthorize("permitAll()")
    @GetMapping("/dashboard")
    public void forwardToDashboardPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/dashboard.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/deleteUser")
    public void forwardToDeleteUserPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/deleteUser.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/deleteSlideshow")
    public void forwardToDeleteSlideshowPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/deleteSlideshow.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/createUser")
    public void forwardToCreateUserPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/createUser.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/updateAboutUs")
    public void forwardToUpdateAboutUsPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/updateAboutUs.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/uploadSlideshow")
    public void forwardUploadSlideShow(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/uploadSlideshow.html");
        requestDispatcher.forward(request, response);
    }
    //about
    //contact
    //home

    @PreAuthorize("permitAll()")
    @GetMapping("/login")
    public void forwardToLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/login.html");
        requestDispatcher.forward(request, response);
    }


    //@PreAuthorize("permitAll()")
    @GetMapping("/")
    public void forwardToHomePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/index.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/about")
    public void forwardToAboutPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/about.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/contact")
    public void forwardToContactPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/contact.html");
        requestDispatcher.forward(request, response);
    }
}