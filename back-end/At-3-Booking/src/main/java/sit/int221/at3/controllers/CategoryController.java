package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.category.CategoryUpdateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.dtos.lecturerMapping.ShowLecturerDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.services.CategoryService;
import sit.int221.at3.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    // /api/categories [GET]
    @GetMapping("")
    public List<CategoryDto> getCategoryAll(Authentication authentication) {
        return categoryService.getCategoryAll(authentication);
    }

    // /api/categories/{id} [GET]
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Integer id, Authentication authentication) {
        return categoryService.getCategoryById(id, authentication);
    }

    @GetMapping("/{id}/events")
    public List<EventDto> getAllEventInCategory(@PathVariable Integer id, Authentication authentication) {
        return categoryService.getAllEventInCategory(id, authentication);
    }

    @GetMapping("/{id}/users")
    public List<ShowLecturerDto> getAllUserByCategoryId(@PathVariable Integer id) {
        return userService.getLecturerByCategoryOwner(id);
    }

    @PutMapping("/{id}")
    public Category configCategory(@PathVariable("id") Integer id, @Valid @RequestBody CategoryUpdateDto category, Authentication authentication){
        return categoryService.configCategory(id, category, authentication);
    }
}
