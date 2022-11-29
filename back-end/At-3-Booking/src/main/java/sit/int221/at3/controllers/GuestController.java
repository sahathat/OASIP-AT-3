package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.event.BlindEventDto;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.dtos.lecturerMapping.ShowLecturerDto;
import sit.int221.at3.entities.Event;
import sit.int221.at3.services.CategoryService;
import sit.int221.at3.services.EventService;
import sit.int221.at3.services.UserService;

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

    @Autowired
    private UserService userService;

    @GetMapping("/events")
    public List<BlindEventDto> getBlindEventAll(@RequestParam(defaultValue = "eventStartTime") String params) {
        return eventService.getBlindEventAll(params);
    }

    @GetMapping("/events/{id}")
    public BlindEventDto getBlindEventAll(@PathVariable("id") Integer id) {
        return eventService.getBlindEventById(id);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategoryAllByGuest(@RequestParam(defaultValue = "id") String params) {
        return categoryService.getCategoryAllByGuest(params);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryIdByGuest(@PathVariable("id") Integer id){
        return categoryService.getCategoryIdByGuest(id);
    }

    @GetMapping("/categories/{id}/users")
    public List<ShowLecturerDto> getAllUserByCategoryId(@PathVariable Integer id) {
        return userService.getLecturerByCategoryOwner(id);
    }
}
