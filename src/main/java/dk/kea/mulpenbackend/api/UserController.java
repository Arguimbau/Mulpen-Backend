package dk.kea.mulpenbackend.api;

import dk.kea.mulpenbackend.dto.JwtRequestModel;
import dk.kea.mulpenbackend.dto.JwtResponseModel;
import dk.kea.mulpenbackend.JwtTokenManager;
import dk.kea.mulpenbackend.service.IUserService;
import dk.kea.mulpenbackend.service.JwtUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@NoArgsConstructor
public class UserController {
  @Autowired
  private JwtUserDetailsService userDetailsService;
  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenManager jwtTokenManager;
  @Autowired
  private IUserService userService;

  @PostMapping("/login")
  public ResponseEntity<JwtResponseModel> createToken(@RequestBody JwtRequestModel request) throws Exception {
    System.out.println(" JwtController createToken Call: 4" + request.getUsername());
    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(),
          request.getPassword())
      );
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponseModel("Bad credentials"));
    }
    final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    final String jwtToken = jwtTokenManager.generateJwtToken(userDetails);
    return ResponseEntity.ok(new JwtResponseModel(jwtToken));
  }

  @PostMapping("/getSecret")
  public ResponseEntity<Map> getSecret() {
    System.out.println("getSecret is called");
    Map<String, String> map = new HashMap<>();
    map.put("message", "this is secret from server");
    return ResponseEntity.ok(map);
  }
}
