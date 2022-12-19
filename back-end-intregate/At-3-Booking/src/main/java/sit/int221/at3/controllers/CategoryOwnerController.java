package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.lecturerMapping.CategoryMappingDto;
import sit.int221.at3.entities.LecturerMapping;
import sit.int221.at3.entities.Role;
import sit.int221.at3.entities.User;
import sit.int221.at3.repositories.UserRepository;
import sit.int221.at3.services.LecturerCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/mappings")
public class CategoryOwnerController {
    @Autowired
    private LecturerCategoryService lecturerCategoryService;

    @Autowired
    private UserRepository userRepository;

    private final List<SimpleGrantedAuthority> admin = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.admin)));

    @PostMapping("")
    public LecturerMapping mapCategoryOwner(@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {
        // admin case
        if(authentication.getAuthorities().equals(admin)){
            try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                if(user.getRole() == Role.lecturer){
                    return lecturerCategoryService.save(categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot config category owner in email other roles except lecturer only");
                }
            } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "other role except admin cannot config category owner");
        }
    }

    @PutMapping("/{categoryId}")
    public LecturerMapping configMapCategoryOwner(@PathVariable Integer categoryId ,@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {
        // admin case
        if(authentication.getAuthorities().equals(admin)){
            try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                if(user.getRole() == Role.lecturer){
                    return lecturerCategoryService.update(categoryId,categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot config category owner in email other roles except lecturer only");
                }
            } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "other role except admin cannot config category owner");
        }
    }

    @DeleteMapping("/{categoryId}")
    public void deleteMapCategoryOwner(@PathVariable Integer categoryId ,@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {
        // admin case
        if(authentication.getAuthorities().equals(admin)){
            try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                if(user.getRole() == Role.lecturer){
                    lecturerCategoryService.delete(categoryId,categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot config category owner in email other roles except lecturer only");
                }
            } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "other role except admin cannot config category owner");
        }
    }
}
