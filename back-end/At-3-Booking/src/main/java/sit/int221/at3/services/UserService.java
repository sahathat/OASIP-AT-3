package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.user.UserDto;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.utils.ListMapper;

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
}
