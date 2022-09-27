package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.dtos.event.EventUpdateDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.Role;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.utils.ListMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<EventDto> getEventAll(String params) {
        // use List event sorted by datetime parameter by descendant order
        List<Event> event = eventRepository.findAll(Sort.by(params).descending());

        return listMapper.mapList(event, EventDto.class, modelMapper);
    }

    public EventDto getEventById(Integer id) {
        // find event id
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
        );
        return modelMapper.map(event, EventDto.class);
    }

    public List<EventDto> getEventUpcoming(){
        List<Event> event = eventRepository.getEventUpcoming();
        return listMapper.mapList(event,EventDto.class,modelMapper);
    }

    public List<EventDto> getEventPast(){
        List<Event> event = eventRepository.getEventPast();
        return listMapper.mapList(event,EventDto.class,modelMapper);
    }

    public List<EventDto> getEventDay(LocalDate date){
        List<Event> event = eventRepository.getEventListDay(date);
        return listMapper.mapList(event,EventDto.class,modelMapper);
    }

    public List<EventDto> getEventToday(){
        List<Event> event = eventRepository.getEventListToday();
        return listMapper.mapList(event,EventDto.class,modelMapper);
    }

    public Event save(EventCreateDto newEvent) {

        // find category id if new event are created
        Category category = categoryRepository.findById(newEvent.getEventCategoryId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, newEvent.getEventCategoryId() + " is not exist please find new id if exist.")
        );

        // set event category duration in event duration
        newEvent.setEventDuration(category.getEventCategoryDuration());

        // mapped event if created
        Event event = modelMapper.map(newEvent, Event.class);

        // check if date on category is nonoverlap
        if (!checkNonOverLap(event, category)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "datetime should non overlap by categories");
        }

        return eventRepository.saveAndFlush(event);
    }

    public void delete(Integer id, Authentication authentication) {


        // check id if not found then throw exception.
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // check email by authorize student role
        List<SimpleGrantedAuthority> student = Arrays.asList(new SimpleGrantedAuthority(String.valueOf("ROLE_"+ Role.student)));

        if (authentication.getAuthorities().equals(student) && !authentication.getName().equals(event.getBookingEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "id of email event should same by student email");
        }

        // and delete id.
        eventRepository.deleteById(id);
    }

    // update PUT
    private Event mapEvent(Event existingEvent, EventUpdateDto updateEvent) {
        // fields who update
        existingEvent.setEventStartTime(updateEvent.getEventStartTime());
        existingEvent.setEventNotes(updateEvent.getEventNotes());
        return existingEvent;
    }

    public Event update(Integer id, EventUpdateDto updateEvent, Authentication authentication) {
        // check id if not found then throw exception.
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // find id when update by mapped field
        event = eventRepository.findById(id).map(
                o -> mapEvent(o, updateEvent)).orElseGet(() -> {
            updateEvent.setId(id);
            // return modelmapper
            return modelMapper.map(updateEvent, Event.class);
        });

        // check email by authorize student role
        List<SimpleGrantedAuthority> student = Arrays.asList(new SimpleGrantedAuthority(String.valueOf("ROLE_"+ Role.student)));

        if (authentication.getAuthorities().equals(student) && !authentication.getName().equals(event.getBookingEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "id of email event should same by student email");
        }

        // find id of category
        Category category = categoryRepository.findById(event.getEventCategory().getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, " eventcategory id does not exist please find new id if exist."));

        // check if date on category is nonoverlap
        if (!checkNonOverLap(event, category)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "datetime should non overlap by categories");
        }

        // and save
        return eventRepository.saveAndFlush(event);
    }

    public boolean checkNonOverLap(Event newEvent, Category category) {
        // set event list that on category
        Set<Event> eventList = category.getEventList();

        // check that time are no overlap
        final boolean[] isNonOverlap = {true};

        // check that datetime is overlap in each eventList
        eventList.forEach(
                e -> {
                    if (newEvent.getId() != e.getId()) {
                        if (e.getEventStartTime().isBefore(newEvent.getEventStartTime().plusMinutes(e.getEventDuration()))
                                && newEvent.getEventStartTime().isBefore(e.getEventStartTime().plusMinutes(e.getEventDuration()))
                        ) {
                            isNonOverlap[0] = false;
                        }
                    }
                }
        );

        return isNonOverlap[0];
    }
}
