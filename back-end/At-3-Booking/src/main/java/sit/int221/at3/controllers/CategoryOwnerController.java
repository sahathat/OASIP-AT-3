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

    private final List<SimpleGrantedAuthority> lecturer = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.lecturer)));

    private final List<SimpleGrantedAuthority> admin = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.admin)));

    @PostMapping("")
    public LecturerMapping mapCategoryOwner(@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {
        // lecturer case
        if(authentication.getAuthorities().equals(lecturer)){
            if(authentication.getName().equals(categoryMappingDto.getEmail())){
                return lecturerCategoryService.save(categoryMappingDto);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "email must matching");
            }
        // admin case
        } else if(authentication.getAuthorities().equals(admin)){
            try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                if(user.getRole() == Role.lecturer){
                    return lecturerCategoryService.save(categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot add category owner");
                }
            } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
            }
        }
        return null;
    }

    @PutMapping("/{categoryId}")
    public LecturerMapping configMapCategoryOwner(@PathVariable Integer categoryId ,@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {

            // lecturer case
            if(authentication.getAuthorities().equals(lecturer)){
                if(authentication.getName().equals(categoryMappingDto.getEmail())){
                    return lecturerCategoryService.update(categoryId,categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "email must matching");
                }
            // admin case
            } else if(authentication.getAuthorities().equals(admin)){
                try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                    if(user.getRole() == Role.lecturer){
                        return lecturerCategoryService.update(categoryId,categoryMappingDto);
                    } else {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot update category owner");
                    }
                } catch (NullPointerException ex) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
                }
            }
        return null;
    }

    @DeleteMapping("/{categoryId}")
    public void deleteMapCategoryOwner(@PathVariable Integer categoryId ,@RequestBody CategoryMappingDto categoryMappingDto, Authentication authentication) {
        // lecturer case
        if(authentication.getAuthorities().equals(lecturer)){
            if(authentication.getName().equals(categoryMappingDto.getEmail())){
                lecturerCategoryService.delete(categoryId,categoryMappingDto);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "email must matching");
            }
            // admin case
        } else if(authentication.getAuthorities().equals(admin)){
            try {
                User user = userRepository.findByEmail(categoryMappingDto.getEmail());
                if(user.getRole() == Role.lecturer){
                    lecturerCategoryService.delete(categoryId,categoryMappingDto);
                } else {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "admin cannot delete category owner");
                }
            } catch (NullPointerException ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this email does not exist");
            }
        }
    }
}
