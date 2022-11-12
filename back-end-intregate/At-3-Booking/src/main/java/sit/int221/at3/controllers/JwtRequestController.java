package sit.int221.at3.controllers;

import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.user.JwtResponse;
import sit.int221.at3.dtos.user.UserLoginDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.ConfirmUser;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.services.ConfirmUserService;
import sit.int221.at3.services.UserService;
import sit.int221.at3.utils.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class JwtRequestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private ConfirmUserService confirmUserService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/api/users/signin", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UserLoginDto authenticationRequest)
            throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        }
        catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        UserDetails userdetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        String token = jwtUtil.generateToken(userdetails);

        User user = userRepository.findByEmail(jwtUtil.getUsernameFromToken(token));

        return ResponseEntity.ok(new JwtResponse(token, user.getName() , user.getEmail(), String.valueOf(jwtUtil.getRolesFromToken(token))));
    }

    @RequestMapping(value = "/api/users/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshtoken(HttpServletRequest request) throws Exception {
        // From the HttpRequest get the claims
        DefaultClaims claims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
        if(claims == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "need expire access token for use refresh token");
        }
        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtUtil.doGenerateRefreshToken(expectedMap, expectedMap.get("sub").toString());

        User user = userRepository.findByEmail(jwtUtil.getUsernameFromToken(token));

        return ResponseEntity.ok(new JwtResponse(token, user.getName(), user.getEmail(), String.valueOf(jwtUtil.getRolesFromToken(token))));
    }

    public Map<String, Object> getMapFromIoJsonwebtokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    @RequestMapping(value = "/api/users/signup", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public User saveUser(@Valid @RequestBody UserModifyDto user){
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16,27,2,4096,10);
        user.setPassword(encoder.encode(user.getPassword()));
        return userDetailsService.signupUser(user);
    }

    @RequestMapping(value = "/api/users/reset_token", method = RequestMethod.POST)
    public String resetToken(@RequestBody UserModifyDto userModifyDto) {
        try {
            // find user by email
            User user = userRepository.findByEmail(userModifyDto.getEmail());
            ConfirmUser confirmUser = confirmUserService.findByUser(user);

            // update token when token is expire
            String updateToken = confirmUserService.updateToken(confirmUser);

            // get user
            return updateToken;
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
        }
    }

    @RequestMapping(value = "/api/users/confirm", method = RequestMethod.GET)
    public String changeRoleWhenConfirm(@RequestParam("token") String token) {
        try {
            // get token if found
            ConfirmUser confirmUser = confirmUserService.findByToken(token);
            User user = confirmUser.getUser();

            // if token is not expire
            if(confirmUser.getExpireDate().isAfter(ZonedDateTime.now())) {
                // change role when email has been confirmed
                user.setRole(confirmUser.getRole());
                // and save role
                userRepository.saveAndFlush(user);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"this token is expired");
            }
            return "email has been confirm";
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"token is invalid");
        }
    }

    @RequestMapping(value = "/api/users/verify", method = RequestMethod.POST)
    public String verifyEmail(@RequestBody UserModifyDto userModifyDto) {
        try {
            // find user by email
            User user = userRepository.findByEmail(userModifyDto.getEmail());
            ConfirmUser confirmUser = confirmUserService.findByUser(user);
            confirmUserService.sendMailForConfirm(user.getEmail(), confirmUser.getToken());
            return "Your email has been sent";
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
        }
    }

    @RequestMapping(value = "/api/users/reset_password", method = RequestMethod.PUT)
    public String resetPassword(@RequestParam("token") String token, @RequestBody UserModifyDto userModifyDto){
        try {
            // find user by token
            ConfirmUser confirmUser = confirmUserService.findByToken(token);

            // find user by email
            User user = userRepository.findByEmail(confirmUser.getUser().getEmail());

            // if token is not expire
            if(confirmUser.getExpireDate().isAfter(ZonedDateTime.now())){
                // password has been reset
                Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder(16,27,2,4096,10);
                user.setPassword(passwordEncoder.encode(userModifyDto.getPassword()));
                userRepository.saveAndFlush(user);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"this token is expired");
            }
            return "password has been reset";
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"token is invalid");
        }
    }
}
