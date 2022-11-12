package sit.int221.at3.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.entities.ConfirmUser;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.ConfirmUserRepository;
import sit.int221.at3.repositories.UserRepository;

import javax.mail.MessagingException;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class ConfirmUserService {
    @Autowired
    private ConfirmUserRepository confirmUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public ConfirmUser findByToken(String token){
        try {
            return confirmUserRepository.findByToken(token);
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this token is unused");
        }
    }

    public ConfirmUser findByUser(User user) {
        try {
            return confirmUserRepository.findByUser(user);
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this"+ ex + "does not exist");
        }
    }

    public String generateVerifyToken(User user){
        String token = UUID.randomUUID().toString();
        saveToken(user,token);
        return token;
    }

    public ConfirmUser saveToken(User user,String token){
        ConfirmUser newVerification = new ConfirmUser(token, user, user.getRole());
        newVerification.setExpireDate(ZonedDateTime.now().plusHours(24));
        return confirmUserRepository.saveAndFlush(newVerification);
    }

    public String updateToken(ConfirmUser user){
        if (user.getExpireDate().isBefore(ZonedDateTime.now())) {
                // generate token for this email and save
                user = confirmUserRepository.findById(user.getId()).orElseGet(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,"user id dose not exist");
                });
                user.setToken(UUID.randomUUID().toString());
                user.setExpireDate(ZonedDateTime.now().plusHours(24));
                confirmUserRepository.saveAndFlush(user);
        }
        return user.getToken();
    }

    public void deleteUserConfirm(User user){
        try {
            ConfirmUser confirmUser = confirmUserRepository.findByUser(user);
            confirmUserRepository.deleteById(confirmUser.getId());
        } catch (NullPointerException ex) {
            userRepository.deleteById(user.getId());
        }
    }

    public void sendMailForConfirm(String email,String token) {
        // email sending verify
        String to = email;
        String link = "http://localhost:8443/api/users/confirm?token=" + token;
        String subject = "[OASIP] Confirm you sign up with email";
        String htmlBody = "<h2>Congratulation: verify email</h2>" +
                "<p>please click <a href=" + link + ">This link</a></p>";

        // send the email
        try {
            emailService.sendSimpleEmail(to,subject,htmlBody);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this email not found");
        };
    }
}
