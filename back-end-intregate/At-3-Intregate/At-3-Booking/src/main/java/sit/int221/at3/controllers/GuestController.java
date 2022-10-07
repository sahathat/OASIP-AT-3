package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.entities.Event;
import sit.int221.at3.services.CategoryService;
import sit.int221.at3.services.EventService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventService eventService;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody EventCreateDto newEvent) throws MessagingException {
        return eventService.save(newEvent);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategoryAllByGuest(@RequestParam(defaultValue = "id") String params) {
        return categoryService.getCategoryAllByGuest(params);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryIdByGuest(@PathVariable("id") Integer id){
        return categoryService.getCategoryIdByGuest(id);
    }
}
