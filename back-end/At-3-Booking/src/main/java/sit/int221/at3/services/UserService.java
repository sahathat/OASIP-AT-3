package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.ConfirmUser;
import sit.int221.at3.entities.Role;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.utils.ListMapper;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmUserService confirmUserService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<UserDto> getUserAll(String params) {
        // use List event sorted by datetime parameter by descendant order
        List<User> users = userRepository.findAll(Sort.by(params));

        return listMapper.mapList(users, UserDto.class, modelMapper);
    }

    public UserDto getUserById(Integer id) {
        User users = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
        );
        return modelMapper.map(users, UserDto.class);
    }

    public User saveUser(UserModifyDto newUser) {
        // set default role when null
        if (newUser.getRole() == null) {
            newUser.setRole(Role.student);
        }

        // mapped event if created
        User user = modelMapper.map(newUser, User.class);

        // check if user on category is not unique
        if (checkIsNotUnique(user, newUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name or email must unique");
        }

        // create user
        User createdUser = userRepository.saveAndFlush(user);

        // generate token
        confirmUserService.generateVerifyToken(user);

        // create user
        return createdUser;
    }

    public User signupUser(UserModifyDto newUser){
        // create user and confirm
        User user = saveUser(newUser);

        // set role to guest
        user.setRole(Role.guest);

        // find token
        ConfirmUser confirmUser = confirmUserService.findByUser(user);

        // send to confirm
        confirmUserService.sendMailForConfirm(user.getEmail(), confirmUser.getToken());

        return userRepository.saveAndFlush(user);
    }

    public void deleteUser(Integer id, Authentication authentication) {
        // check id if not found then throw exception.
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // cannot delete yourself
        if(authentication.getName().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"admin cannot delete yourself");
        }

        // delete confirm user and user
        confirmUserService.deleteUserConfirm(user);
    }

    // update PUT
    private User mapUser(User existingUser, UserModifyDto updateUser) {
        // fields who update
        existingUser.setName(updateUser.getName());
        existingUser.setEmail(updateUser.getEmail());
        existingUser.setRole(updateUser.getRole());
        return existingUser;
    }

    public User updateUser(Integer id, UserModifyDto updateUser) {
        // check id if not found then throw exception.
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // check if user on category is not unique
        if (checkIsNotUnique(user, updateUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name or email must unique");
        }

        // find id when update by mapped field
        user = userRepository.findById(id).map(
                o -> mapUser(o, updateUser)).orElseGet(() -> {
            updateUser.setId(id);
            // return modelmapper
            return modelMapper.map(updateUser, User.class);
        });

        // and save
        return userRepository.saveAndFlush(user);
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<SimpleGrantedAuthority> roles = null;
        // List all user
        List<User> userList = userRepository.findAll();

        // init id is null if can't find that return 404 not found
        Integer[] id = { 0 };

        // check if email equal List of email
        userList.forEach(u -> {
            if(u.getEmail().equals(email)){
                id[0] = u.getId();
            }
        });

        // find id if user found
        User user = userRepository.findById(id[0]).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "this email not found. please created"));

        // keep in roles and user details
        roles = Arrays.asList(new SimpleGrantedAuthority(String.valueOf(user.getRole())));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), roles);
    }

    private boolean checkIsNotUnique(User user, UserModifyDto updateUser) {
        // List all categories
        List<User> users = userRepository.findAll();

        // loop check categories by categories id
        boolean[] isNotUnique = {false};
        users.forEach(
                e -> {
                    if (user.getId() != e.getId()) {
                        // check by item of category name by lowercase that equal choose category name by lowercase
                        if (updateUser.getName().toLowerCase().equals(e.getName().toLowerCase()) ||
                                updateUser.getEmail().toLowerCase().equals(e.getEmail().toLowerCase())) {
                            isNotUnique[0] = true;
                        }
                    }
                }
        );

        return isNotUnique[0];
    }
}
