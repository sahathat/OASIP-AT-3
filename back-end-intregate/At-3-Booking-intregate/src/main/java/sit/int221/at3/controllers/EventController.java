package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.dtos.event.EventUpdateDto;
import sit.int221.at3.entities.Event;
import sit.int221.at3.services.EventService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired

    private EventService eventService;

    // /api/events [GET]
    @CrossOrigin
    @GetMapping("")
    public List<EventDto> getEventAll(
            @RequestParam(defaultValue = "eventStartTime") String params) {
        return eventService.getEventAll(params);
    }

    // /api/events/{id} [GET]
    @CrossOrigin
    @GetMapping("/{id}")
    public EventDto getEventById (@PathVariable Integer id) {
        return eventService.getEventById(id);
    }

    @CrossOrigin
    @GetMapping("/upcoming")
    public List<EventDto> getEventUpcoming(){
        return eventService.getEventUpcoming();
    }

    @CrossOrigin
    @GetMapping("/past")
    public List<EventDto> getEventPast(){
        return eventService.getEventPast();
    }

    @CrossOrigin
    @GetMapping("/day/{date}")
    public List<EventDto> getEventDay(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        System.out.println(date);
        return eventService.getEventDay(date);
    }

    @CrossOrigin
    @GetMapping("/day")
    public List<EventDto> getEventToday(){
        return eventService.getEventToday();
    }

    // /api/events [POST]
    @CrossOrigin
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@Valid @RequestBody EventCreateDto newEvent) {
        return eventService.save(newEvent);
    }

    // /api/events/{id} [PUT]
    @CrossOrigin
    @PutMapping("/{id}")
    public Event putUpdate(@PathVariable("id") Integer id,@Valid @RequestBody EventUpdateDto event) {
        return eventService.update(id, event);
    }

    // /api/events/{id} [DELETE]
    @CrossOrigin
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        eventService.delete(id);
    }
}
