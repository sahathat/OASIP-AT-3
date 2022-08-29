package sit.int222.testauthenticate.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
//    @Autowired
//    PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String password = "ABC123";
        Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
        return new User("Test@email.com", "$argon2id$v=19$m=4096,t=3,p=1$BijEalA2m84l/GXyAg8w9w$QJuvolKdWRQq4w2r3DbbafM+6M7JfV//f74pJD7zRLw",new ArrayList<>());
    }
}
