package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.category.CategoryDto;
import sit.int221.at3.dtos.category.CategoryUpdateDto;
import sit.int221.at3.dtos.event.EventDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.Event;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.utils.ListMapper;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ListMapper listMapper;

    public List<CategoryDto> getCategoryAll(String param){
        List<Category> categoryList = categoryRepository.findAll(Sort.by(param).descending());
        return listMapper.mapList(categoryList,CategoryDto.class,modelMapper);
    }

    public CategoryDto getCategoryById(Integer id){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist")
        );
        return modelMapper.map(category,CategoryDto.class);
    }

    public List<EventDto> getAllEventInCategory(Integer id){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist")
        );

        List<Event> events = eventRepository.getEventInCategory(id);

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

    public Category configCategory(Integer id, CategoryUpdateDto updatedCategory){

        // check id category
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " does not exist please find new id if exist"));

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
}
