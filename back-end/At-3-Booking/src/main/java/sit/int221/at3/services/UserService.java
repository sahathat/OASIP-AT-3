package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.category.CategoryUpdateDto;
import sit.int221.at3.dtos.event.EventUpdateDto;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.Role;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.utils.ListMapper;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ListMapper listMapper;

    public List<UserDto> getUserAll(String params) {
        // use List event sorted by datetime parameter by descendant order
        List<User> users = userRepository.findAll(Sort.by(params));

        return listMapper.mapList(users, UserDto.class, modelMapper);
    }

    public UserDto getUserById(Integer id){
        User users = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
        );
        return modelMapper.map(users,UserDto.class);
    }

    public User saveUser(UserModifyDto newUser) {

        // set default role when null
        if(newUser.getRole() == null){
            newUser.setRole(Role.student);
        }

        // if user input event by string " input it " that return "input it"
        newUser.setName(newUser.getName().trim());
        newUser.setEmail(newUser.getEmail().trim());

        // mapped event if created
        User user = modelMapper.map(newUser, User.class);

        // check if user on category is not unique
        if (checkIsNotUnique(user, newUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name or email must unique");
        }

        // insert in repository
        return userRepository.saveAndFlush(user);
    }

    public void deleteUser(Integer id) {
        // check id if not found then throw exception.
        userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // and delete id.
        userRepository.deleteById(id);
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
        // if user input event by string " input it " that return "input it"
        updateUser.setName(updateUser.getName().trim());
        updateUser.setEmail(updateUser.getEmail().trim());

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

    private boolean checkIsNotUnique(User user, UserModifyDto updateUser){
        // List all categories
        List<User> users = userRepository.findAll();

        // loop check categories by categories id
        boolean[] isNotUnique = {false};
        users.forEach(
                e -> {
                    if (user.getId() != e.getId()) {
                        // check by item of category name by lowercase that equal choose category name by lowercase
                        if (updateUser.getName().toLowerCase().equals(e.getName().toLowerCase()) ||
                                updateUser.getEmail().toLowerCase().equals(e.getEmail().toLowerCase())){
                            isNotUnique[0] = true;
                        }
                    }
                }
        );

        return isNotUnique[0];
    }
}
