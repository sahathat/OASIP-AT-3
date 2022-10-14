package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.dtos.user.UserLoginDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.User;
import sit.int221.at3.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/userList")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping()
    public List<UserDto> getUserAll(
            @RequestParam(defaultValue = "name") String params){
        return userService.getUserAll(params);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") Integer id){
        return userService.getUserById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public User saveUser(@Valid @RequestBody UserModifyDto user){
        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16,27,2,4096,10);
        user.setPassword(encoder.encode(user.getPassword()));
        return userService.saveUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id,@RequestBody UserModifyDto updateUser){
        return userService.updateUser(id,updateUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@Valid @PathVariable Integer id){
        userService.deleteUser(id);
    }

//    @PostMapping("/matched")
//    public boolean saveUser(@RequestBody UserLoginDto user) throws ResponseStatusException{
//        Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(16,27,2,4096,10);
//
//        User userbyEmail = userService.findUserByEmail(user);
//
//        // check if password that find by this email had encoded is match by raw password
//        boolean isMatched = encoder.matches(user.getPassword(), userbyEmail.getPassword());
//
//        if(!isMatched){
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "email or password is not matched");
//        }
//
//        return isMatched;
//    }
}
