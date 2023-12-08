package dk.kea.mulpenbackend.api;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

@Controller
@EnableWebMvc
public class ViewController {

    @GetMapping("/videos")
    public void forwardToViewVideoPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/viewVideo.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/upload")
    public void forwardToUploadMediaPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/uploadMedia.html");
        requestDispatcher.forward(request, response);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/dashboard")
    public void forwardToDashboard(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/dashboard.html");
        requestDispatcher.forward(request, response);
    }

    //about
    //contact
    //home

    @GetMapping("/login")
    public void forwardToLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/login.html");
        requestDispatcher.forward(request, response);
    }


    @GetMapping("/")
    public void forwardToHomePage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/index.html");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/about")
    public void forwardToAboutPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/about.html");
        requestDispatcher.forward(request, response);
    }

    @GetMapping("/contact")
    public void forwardToContactPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        ServletContext context = request.getServletContext();
        RequestDispatcher requestDispatcher = context.getRequestDispatcher("/contact.html");
        requestDispatcher.forward(request, response);
    }
}
