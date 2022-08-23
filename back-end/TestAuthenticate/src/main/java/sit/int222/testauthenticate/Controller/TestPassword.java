package sit.int222.testauthenticate.Controller;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class TestPassword {

    @GetMapping("/encrypt")
    public String encrypt() {
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();

        String password = "ABC123";

        String hash = encoder.encode(password);

        return hash;
    }

    @PostMapping("/match")
    public String match(@RequestBody String rawPassword) {
        String hash = encrypt();
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder();
        boolean matches = encoder.matches(rawPassword,hash);
        if(!matches){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "password does not matched");
        }
        return "yay!!";
    }
}
