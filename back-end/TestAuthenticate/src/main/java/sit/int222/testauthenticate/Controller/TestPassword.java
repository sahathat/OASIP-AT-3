package sit.int222.testauthenticate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int222.testauthenticate.Service.UserService;
import sit.int222.testauthenticate.dto.Response;
import sit.int222.testauthenticate.dto.User;
import sit.int222.testauthenticate.utils.JwtUtil;

@RestController
@RequestMapping("/api")
public class TestPassword {

    @GetMapping("/user/encrypt")
    public String encrypt() {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16,27,2,4096,10);

        String password = "ABC123";

        String hash = encoder.encode(password);

        return hash;
    }

    @PostMapping("/user/match")
    public String match(@RequestBody User user) {
        String hash = encrypt();
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        boolean matches = encoder.matches(user.getPassword(), hash);
        if(!matches){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "password does not matched");
        }
        return "yay!!";
    }

    @RequestMapping(value = "/hello")
    public String hello(){
        return "hello";
    }
}
