package sit.int221.at3.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.dtos.lecturerMapping.CategoryMappingDto;
import sit.int221.at3.entities.Category;
import sit.int221.at3.entities.LecturerMapping;
import sit.int221.at3.entities.Role;
import sit.int221.at3.repositories.CategoryRepository;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.repositories.LecturerMappingRepository;

import java.util.List;

@Service
public class LecturerCategoryService {
    @Autowired
    private EventRepository UserRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LecturerMappingRepository lecturerMappingRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final List<SimpleGrantedAuthority> lecturer = List.of(new SimpleGrantedAuthority(String.valueOf("ROLE_" + Role.lecturer)));

    public LecturerMapping save(CategoryMappingDto mapping) {
        // find id if exist
        Category category = categoryRepository.findById(mapping.getCategoryId()).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this category id does not exist");
        });

        // find id if exist mapping
        List<LecturerMapping> mappingList = lecturerMappingRepository.getLecturerMappingByCategory(category.getId());

        // check all list of category if categories has been same by input they must have bad request
        mappingList.forEach(ml -> {
            if(ml.getEmail().equals(mapping.getEmail()) && ml.getCategory().getId().equals(mapping.getCategoryId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this category id have been exist");
            }
        });

        // map List and save
        LecturerMapping newMapping = modelMapper.map(mapping, LecturerMapping.class);
        newMapping.setMappingId(null);
        return lecturerMappingRepository.saveAndFlush(newMapping);
    }

    public LecturerMapping update(Integer categoryId,CategoryMappingDto mapping) {
        // find category id and mapping category id if exist
        Category newCategory = categoryRepository.findById(mapping.getCategoryId()).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this mapping category id does not exist");
        });
        LecturerMapping newMapping = findByIdToMapOwner(categoryId, mapping.getEmail());

        // check if newMapping has same by other categories
        List<LecturerMapping> emailMappings = lecturerMappingRepository.getLecturerMappingByEmail(mapping.getEmail());
        emailMappings.forEach(lm -> {
            if(lm.getCategory().equals(newCategory)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this category id have been exist");
            }
        });

        // map List and save
        newMapping.setCategory(newCategory);
        return lecturerMappingRepository.saveAndFlush(newMapping);
    }

    public void delete(Integer categoryId,CategoryMappingDto mapping) {
        // find mapping
        LecturerMapping existingMapping = findByIdToMapOwner(categoryId, mapping.getEmail());

        // delete
        lecturerMappingRepository.deleteById(existingMapping.getMappingId());
    }

    public LecturerMapping findByIdToMapOwner(Integer categoryId, String email) {
        // find category id if exist
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(()->{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this mapping category id does not exist");
        });

        // find lecturer mapping if exist
        LecturerMapping newMapping = lecturerMappingRepository.findByCategoryAndEmail(existingCategory, email);

        // case email and category does not match
        if (newMapping == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"this email does not exist");
        }

        // find category id by path if exist mapping
        List<LecturerMapping> mappingList = lecturerMappingRepository.getLecturerMappingByCategory(categoryId);
        // check all list if this category has 1 owner they must have bad request for change owner
        if (mappingList.size() <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "this category must have at least 1 owner");
        }
        return newMapping;
    }
}
