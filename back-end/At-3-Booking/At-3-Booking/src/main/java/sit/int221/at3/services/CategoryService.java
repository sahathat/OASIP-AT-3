package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.CategoryDto;
import sit.int221.at3.dtos.EventDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.utils.ListMapper;

import java.util.List;
import java.util.Set;

@Service
public class CategoryService {
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
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist.")
        );
        return modelMapper.map(category,CategoryDto.class);
    }

    public Set<EventDto> getAllEventInCategory(Integer id){
        Category category = categoryRepository.findById(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND,id +" is Not Exist Please find new id if exist.")
        );
        return listMapper.mapSet(category.getEventList(),EventDto.class,modelMapper);
    }
}
