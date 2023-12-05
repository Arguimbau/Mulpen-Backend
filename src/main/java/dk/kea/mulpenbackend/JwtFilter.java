package dk.kea.mulpenbackend;

import dk.kea.mulpenbackend.service.JwtUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class JwtFilter extends OncePerRequestFilter {
    private JwtUserDetailsService userDetailsService;
    private JwtTokenManager jwtTokenManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtFilter doFilterInternal start - request header: " + request.getHeader("Authorization"));
        String tokenHeader = request.getHeader("Authorization");
        System.out.println("JwtFilter doFilterInternal call 3 request header: " + tokenHeader);

        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            System.out.println("Token extracted: " + token);
            try {
                String username = jwtTokenManager.getUsernameFromToken(token);
                validateToken(request, username, token);
            } catch (Exception e) {
                System.out.println("Unable to get JWT Token: " + e.getMessage());
            }
        } else {
            // For anonymous users, proceed without checking the token
            System.out.println("No or invalid Authorization header for this request. Proceeding for anonymous user.");
            filterChain.doFilter(request, response);
        }
    }

    private void validateToken(HttpServletRequest request, String username, String token) {
        if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenManager.validateJwtToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken
                        authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }
}