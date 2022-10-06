package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.dtos.event.EventUpdateDto;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.Role;
import sit.int221.at3.services.EventService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    private final List<SimpleGrantedAuthority> student = Arrays.asList(new SimpleGrantedAuthority(String.valueOf("ROLE_"+Role.student)));

    private final List<SimpleGrantedAuthority> lecturer = Arrays.asList(new SimpleGrantedAuthority(String.valueOf("ROLE_"+Role.lecturer)));

    // /api/events [GET]
    @GetMapping("")
    public List<EventDto> getEventAll(
            @RequestParam(defaultValue = "eventStartTime") String params, Authentication authentication) {
        List<EventDto> events = eventService.getEventAll(params);

        if(authentication.getAuthorities().equals(student)) {
            events = events.stream().filter(u -> authentication.getName().equals(u.getBookingEmail())).collect(Collectors.toList());
        }

        return events;
    }

    // /api/events/{id} [GET]
    @GetMapping("/{id}")
    public EventDto getEventById (@PathVariable Integer id, Authentication authentication) {
        EventDto event = eventService.getEventById(id);

        if (authentication.getAuthorities().equals(student) && !event.getBookingEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student email is not same to booking email");
        }

        return event;
    }

    @GetMapping("/upcoming")
    public List<EventDto> getEventUpcoming(Authentication authentication){
        if (authentication.getAuthorities().equals(student)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student should not see in filter method");
        }

        return eventService.getEventUpcoming();
    }

    @GetMapping("/past")
    public List<EventDto> getEventPast(Authentication authentication){
        if (authentication.getAuthorities().equals(student)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student should not see in filter method");
        }

        return eventService.getEventPast();
    }

    @GetMapping("/day/{date}")
    public List<EventDto> getEventDay(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Authentication authentication) {

        if (authentication.getAuthorities().equals(student)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student should not see in filter method");
        }

        return eventService.getEventDay(date);
    }

    @GetMapping("/day")
    public List<EventDto> getEventToday(Authentication authentication){

        if (authentication.getAuthorities().equals(student)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student should not see in filter method");
        }

        return eventService.getEventToday();
    }

    // /api/events [POST]
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody EventCreateDto newEvent, Authentication authentication) {
        if (authentication.getAuthorities().equals(lecturer)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        } else if (authentication.getAuthorities().equals(student) && !authentication.getName().equals(newEvent.getBookingEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "new event should add by student email");
        }
        return eventService.save(newEvent);
    }

    // /api/events/{id} [PUT]
    @PutMapping("/{id}")
    public Event putUpdate(@PathVariable("id") Integer id,@Valid @RequestBody EventUpdateDto event, Authentication authentication) {
        return eventService.update(id, event, authentication);
    }

    // /api/events/{id} [DELETE]
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id, Authentication authentication) {
        eventService.delete(id, authentication);
    }
}
