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
import sit.int221.at3.dtos.lecturerMapping.ShowLecturerDto;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.*;
import sit.int221.at3.repositories.LecturerMappingRepository;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.utils.ListMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LecturerMappingRepository lecturerMappingRepository;

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

    public List<ShowLecturerDto> getLecturerByCategoryOwner(Integer categoryId){
        // get email by category owner id
        List<LecturerMapping> lecturerWithSubject = lecturerMappingRepository.getLecturerMappingByCategory(categoryId);

        // check if category owner not found
        if (lecturerWithSubject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this category id does not exist");
        }

        // initial value
        List<User> users = new ArrayList<>();

        // add user with email lecturer mapping
        lecturerWithSubject.forEach(l -> {
            users.add(userRepository.findByEmail(l.getEmail()));
        });

        // sort user by name ascending order
        List<User> finalUsers = new ArrayList<>(users).stream()
                .sorted(Comparator.comparing(User::getName)).collect(Collectors.toList());
        return listMapper.mapList(finalUsers, ShowLecturerDto.class, modelMapper);

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

        // save user before send email
        user = userRepository.saveAndFlush(user);

        // find token
        ConfirmUser confirmUser = confirmUserService.findByUser(user);

        // send to confirm
        confirmUserService.sendMailForConfirm(user.getEmail(), confirmUser.getToken());

        return user;
    }

    public void deleteUser(Integer id, Authentication authentication) {
        // check id if not found then throw exception.
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // cannot delete yourself
        if(authentication.getName().equals(user.getEmail())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"admin cannot delete yourself");
        }

        // case lecturer have 1 own category
        if(user.getRole() == Role.lecturer){
            List<LecturerMapping> mapping = lecturerMappingRepository.getLecturerMappingByEmail(user.getEmail());

            // check all category if it has 1 owner
            mapping.forEach(lm -> {
                List<LecturerMapping> mappingListByCategory = lecturerMappingRepository.getLecturerMappingByCategory(lm.getCategory().getId());
                if (mappingListByCategory.size() <= 1) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "lecturer at least 1 category owner can not delete");
                }
            });

            // delete all this email in mapping
            mapping.forEach(ml -> {
                lecturerMappingRepository.deleteById(ml.getMappingId());
            });
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
        roles = List.of(new SimpleGrantedAuthority(String.valueOf(user.getRole())));
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
