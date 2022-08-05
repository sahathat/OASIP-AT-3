package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.CategoryDto;
import sit.int221.at3.dtos.EventDto;
import sit.int221.at3.services.CategoryService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/categories")

public class CategoryController {

    @Autowired
    private CategoryService CategoryService;

    // /api/categories [GET]
    @GetMapping("")
    public List<CategoryDto> getCategoryAll(@RequestParam(defaultValue = "id") String params) {
        return CategoryService.getCategoryAll(params);
    }

    // /api/categories/{id} [GET]
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@PathVariable Integer id) {
        return CategoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/events")
    public List<EventDto> getAllEventInCategory(@PathVariable Integer id) {
        return CategoryService.getAllEventInCategory(id);
    }
}
