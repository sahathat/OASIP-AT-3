package sit.int222.testauthenticate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sit.int222.testauthenticate.Service.UserService;
import sit.int222.testauthenticate.dto.Response;
import sit.int222.testauthenticate.dto.User;
import sit.int222.testauthenticate.utils.JwtUtil;

@RestController
public class TestAuthentication {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userDatailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "api/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createToken(@RequestBody User user) throws Exception {

//        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder();

//        user.setPassword(argon2PasswordEncoder.encode(user.getPassword()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"email or password are incorrect");
        }

        System.out.println("Test");
        final UserDetails userDetails = userDatailsService
                .loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new Response(jwt));
    }
}
