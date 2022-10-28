package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.category.CategoryUpdateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.services.CategoryService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/categories")

public class CategoryController {

    @Autowired
    private CategoryService CategoryService;

    // /api/categories [GET]
    @GetMapping("")
    public List<CategoryDto> getCategoryAll(@RequestParam(defaultValue = "id") String params, Authentication authentication) {
        return CategoryService.getCategoryAll(params, authentication);
    }

    // /api/categories/{id} [GET]
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Integer id, Authentication authentication) {
        return CategoryService.getCategoryById(id, authentication);
    }

    @GetMapping("/{id}/events")
    public List<EventDto> getAllEventInCategory(@PathVariable Integer id, Authentication authentication) {
        return CategoryService.getAllEventInCategory(id, authentication);
    }

    @PutMapping("/{id}")
    public Category configCategory(@PathVariable("id") Integer id, @Valid @RequestBody CategoryUpdateDto category, Authentication authentication){
        return CategoryService.configCategory(id, category, authentication);
    }
}
