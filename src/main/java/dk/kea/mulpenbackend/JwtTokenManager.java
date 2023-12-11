package dk.kea.mulpenbackend;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenManager {
    public static final long TOKEN_VALIDITY = 10 * 60 * 60 * 1000; // 10 timer
    // aha: Below is the server's private key. Which is used to generate new tokens. Length: Minimum 512 bits.
    // Which corresponds to minimum 86 characters in cleartext.
    @Value("${SECRET}")
    private String jwtSecret;
    public String generateJwtToken(UserDetails userDetails) {
        System.out.println("TokenManager generateJwtToken(UserDetails) call: 7");

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()); // Assuming getAuthorities() returns a Collection<GrantedAuthority>

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public Boolean validateJwtToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        Claims claims = getClaims(token);
        Boolean isTokenExpired = claims.getExpiration().before(new Date());

        if (username.equals(userDetails.getUsername()) && !isTokenExpired) {
            // Check roles for authorization
            Set<String> roles = (Set<String>) userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            System.out.println("Roles attached to the current JWT token: " + roles);

            // Replace "USER" and "ADMIN" with the actual roles you want to check for
            if (containsAnyRole(roles, "USER", "ADMIN")) {
                // User has the required role for authorization
                return true;
            }
        }
        return false;
    }


    private boolean containsAnyRole(Set<String> roles, String... requiredRoles) {
        if (roles != null && requiredRoles != null) {
            for (String requiredRole : requiredRoles) {
                String roleToCheck = "ROLE_" + requiredRole;
                if (roles.contains(requiredRole)) {
                    return true;
                }
            }
        }
        return false;
    }



    public String getUsernameFromToken(String token) {
        System.out.println("TokenManager getUsernameFromToken(String token) With token: Call: A");
        //Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody(); // before Spring 3
        Claims claims = getClaims(token);
        if(claims != null){
            return claims.getSubject();
        }else {
            return "no user found";
        }
    }

    private Claims getClaims(String token){
        try{
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){
            System.out.println("could not parse JWT token for claims");
        }
        return null;
    }
}
