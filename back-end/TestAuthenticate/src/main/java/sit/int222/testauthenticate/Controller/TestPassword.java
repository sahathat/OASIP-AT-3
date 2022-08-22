package sit.int222.testauthenticate.Controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestPassword {

    @GetMapping("/encrypt")
    public String encrypt() {
        String password = "Tslol05346194!";
        PasswordEncoder encode = new BCryptPasswordEncoder();
        String encodedPassword = encode.encode(password);
        return encodedPassword;
    }

    @GetMapping("/check")
    @ResponseBody
    public boolean check(@RequestParam String rawPassword) {
        String Test = encrypt();
        PasswordEncoder decode = new BCryptPasswordEncoder();
        boolean decodedPassword = decode.matches(rawPassword,Test);
        return decodedPassword;
    }
}
