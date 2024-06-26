package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.event.BlindEventDto;
import sit.int221.at3.dtos.event.EventCreateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.dtos.event.EventUpdateDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.LecturerMapping;
import sit.int221.at3.entities.Role;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.repositories.LecturerMappingRepository;
import sit.int221.at3.utils.ListMapper;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LecturerMappingRepository lecturerMappingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileService fileService;

    private final List<SimpleGrantedAuthority> student = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_"+ Role.student)));

    private final List<SimpleGrantedAuthority> lecturer = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_"+ Role.lecturer)));

    public List<EventDto> getEventAll(String params, Authentication authentication) {
        // use List event sorted by datetime parameter by descendant order
        List<Event> events = eventRepository.findAll(Sort.by(params).descending());

        if(authentication.getAuthorities().equals(student)) {
            events = events.stream().filter(u -> authentication.getName().equals(u.getBookingEmail())).collect(Collectors.toList());
        } else if (authentication.getAuthorities().equals(lecturer)) {
            List<Category> categoryList = categoryRepository.findAll();

            // call lecturer mapping for filter lecturer email by authentication email
            List<LecturerMapping> lecturerMappingList = lecturerMappingRepository.findAll().stream().filter(
                    lm -> authentication.getName().equals(lm.getEmail())).toList();

            // clear all item on categories
            categoryList.clear();

            // add item when filter by categories in lecturer mapping
            lecturerMappingList.forEach(lm -> categoryList.add(lm.getCategory()));

            events.clear();

            // add item when filter by events in categories mapping
            List<Event> finalEvents = events;
            categoryList.forEach(c -> {
                List<Event> eventModels = eventRepository.getEventInCategory(c.getId());
                eventModels.forEach(e -> {
                    finalEvents.add(eventRepository.getById(e.getId()));
                });
            });

            // sorting by eventStartDate
            events = new ArrayList<>(finalEvents).stream()
                    .sorted(Comparator.comparing(Event::getEventStartTime)
                            .reversed()).collect(Collectors.toList());
        }
        return listMapper.mapList(events, EventDto.class, modelMapper);
    }

    public List<BlindEventDto> getBlindEventAll(String params) {
        // use List event sorted by datetime parameter by descendant order
        List<Event> events = eventRepository.findAll(Sort.by(params).descending());
        return listMapper.mapList(events, BlindEventDto.class, modelMapper);
    }

    public EventDto getEventById(Integer id, Authentication authentication) {
        // find event id
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
        );

        if (authentication.getAuthorities().equals(student) && !event.getBookingEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student email is not same to booking email");
        } else if (lecturer.equals(authentication.getAuthorities())) {
            // use lecturer email and categories id
            List<LecturerMapping> lecturerMapping = lecturerMappingRepository
                    .getLecturerMappingByCategory(event.getEventCategory().getId())
                    .stream().filter(lm ->
                            authentication.getName().equals(lm.getEmail())).toList();

            // check if user email not equal owner clinic email sent error 403
            if (lecturerMapping.isEmpty() || !authentication.getName().equals(lecturerMapping.get(0).getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, authentication.getName() + " can see event in owner categories/clinics only");
            }
        }

        return modelMapper.map(event, EventDto.class);
    }

    public BlindEventDto getBlindEventById(Integer id) {
        // find event id
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
        );

        return modelMapper.map(event, BlindEventDto.class);
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

        // email sending models
        String to = newEvent.getBookingEmail();
        String eventTime = newEvent.getEventStartTime().getDayOfWeek() + " " +
                newEvent.getEventStartTime().getMonth() + " " +
                newEvent.getEventStartTime().getDayOfMonth() + " , " +
                newEvent.getEventStartTime().getYear() + " , " +
                newEvent.getEventStartTime().toLocalTime() + " - " +
                newEvent.getEventStartTime().plusMinutes(newEvent.getEventDuration()).toLocalTime() + " (ICT)";
        String subject = "[OASIP] " + category.getEventCategoryName() + " @ " + eventTime;
        String htmlBody = "<b>Booking name: </b>"  + newEvent.getBookingName() + "<br>" +
                "<b>Event categories: </b>" + category.getEventCategoryName() + "<br>" +
                "<b>When: </b>" + eventTime + "<br>" +
                "<b>Event notes: </b>" + (newEvent.getEventNotes() == null ? "" : newEvent.getEventNotes());

        try {
            emailService.sendSimpleEmail(to, subject, htmlBody);
        } catch (MessagingException messagingException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this email not found");
        }

        return eventRepository.saveAndFlush(event);
    }

    public void delete(Integer id, Authentication authentication) {
        // check id if not found then throw exception.
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist."));

        // check email by authorize student role
        if (authentication.getAuthorities().equals(student) && !authentication.getName().equals(event.getBookingEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "id of email event should same by student email");
        }

        // initial value resource
        Resource resource = null;

        // find file is found
        if(event.getEventFile() != null){
            resource = fileService.loadFileAsResource(id, event.getEventFile());
        }

        // if file is found then delete file by id
        if(resource != null) {
            fileService.deleteFileAsResource(id);
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
