package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.dtos.user.UserModifyDto;
import sit.int221.at3.entities.User;
import sit.int221.at3.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
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
    public User saveUser(@Valid @RequestBody UserModifyDto user){
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
}
