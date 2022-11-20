package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.category.CategoryUpdateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;
import sit.int221.at3.entities.LecturerMapping;
import sit.int221.at3.entities.Role;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.repositories.LecturerMappingRepository;
import sit.int221.at3.utils.ListMapper;

import java.util.List;

@Service
public class CategoryService {
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

    private final List<SimpleGrantedAuthority> lecturer = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.lecturer)));

    private final List<SimpleGrantedAuthority> student = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.student)));

    public List<CategoryDto> getCategoryAll(String param, Authentication authentication){
        List<Category> categoryList = categoryRepository.findAll(Sort.by(param).descending());
        // check if user have lecturer role
        if (lecturer.equals(authentication.getAuthorities())) {
            // call lecturer mapping for filter lecturer email by authentication email
            List<LecturerMapping> lecturerMappingList = lecturerMappingRepository.findAll().stream().filter(
                    lm -> authentication.getName().equals(lm.getEmail())).toList();

            // clear all item on categories
            categoryList.clear();

            // add item when filter by categories in lecturer mapping
            lecturerMappingList.forEach(lm -> categoryList.add(lm.getCategory()));
        }
        return listMapper.mapList(categoryList,CategoryDto.class,modelMapper);
    }

    // guest only
    public List<CategoryDto> getCategoryAllByGuest(String param){
        List<Category> categoryList = categoryRepository.findAll(Sort.by(param).descending());
        return listMapper.mapList(categoryList,CategoryDto.class,modelMapper);
    }

    public CategoryDto getCategoryById(Integer id, Authentication authentication){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist")
        );

        // check if user have lecturer role
        authorizeLecturerById(authentication, id, " can see owner categories/clinics only");

        return modelMapper.map(category,CategoryDto.class);
    }

    // guest only
    public CategoryDto getCategoryIdByGuest(Integer id){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist")
        );

        return modelMapper.map(category,CategoryDto.class);
    }

    public List<EventDto> getAllEventInCategory(Integer id, Authentication authentication){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist")
        );

        List<Event> events = eventRepository.getEventInCategory(id);

        // check if user have lecturer role
        authorizeLecturerById(authentication, id, " can see owner categories/clinics only");

        return listMapper.mapList(events,EventDto.class,modelMapper);
    }

    // update PUT
    private Category mapCategory(Category existingCategory, CategoryUpdateDto updateCategory) {
        // fields who update
        existingCategory.setEventCategoryName(updateCategory.getEventCategoryName());
        existingCategory.setEventCategoryDescription(updateCategory.getEventCategoryDescription());
        existingCategory.setEventCategoryDuration(updateCategory.getEventCategoryDuration());
        return existingCategory;
    }

    public Category configCategory(Integer id, CategoryUpdateDto updatedCategory, Authentication authentication){

        // check id category
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist"));

        // check if user have lecturer role
        authorizeLecturerById(authentication,id," can edit owner categories/clinics only");

        if(student.equals(authentication.getAuthorities())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "student cannot edit categories in each clinic");
        }

        // check that is unique
        if(checkIsNotUnique(category,updatedCategory)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categories name must unique");
        }

        // find id when update by mapped field
        category = categoryRepository.findById(id).map(
                o -> mapCategory(o, updatedCategory)).orElseGet(() -> {
            updatedCategory.setId(id);
            // return modelmapper
            return modelMapper.map(updatedCategory, Category.class);
        });

        return categoryRepository.saveAndFlush(category);
    }

    private boolean checkIsNotUnique(Category category,CategoryUpdateDto updateCategory){

        // List all categories
        List<Category> categories = categoryRepository.findAll();

        // loop check categories by categories id
        boolean[] isNotUnique = {false};
        categories.forEach(
                e -> {
                    if (category.getId() != e.getId()) {
                        // check by item of category name by lowercase that equal choose category name by lowercase
                        if (updateCategory.getEventCategoryName().toLowerCase().equals(e.getEventCategoryName().toLowerCase())){
                            isNotUnique[0] = true;
                        }
                    }
                }
        );

        return isNotUnique[0];
    }

    private void authorizeLecturerById(Authentication authentication, Integer id , String errormassage) {
        // check if user have lecturer role
        if (lecturer.equals(authentication.getAuthorities())) {
            // use lecturer email and categories id
            List<LecturerMapping> lecturerMapping = lecturerMappingRepository
                    .getLecturerMappingByCategory(id)
                    .stream().filter(lm ->
                            authentication.getName().equals(lm.getEmail())).toList();

            // check if user email not equal owner clinic email sent error 403
            if (lecturerMapping.isEmpty() || !authentication.getName().equals(lecturerMapping.get(0).getEmail())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, authentication.getName() + errormassage);
            }
        }
    }
}
