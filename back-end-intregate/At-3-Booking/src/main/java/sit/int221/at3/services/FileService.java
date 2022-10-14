package sit.int221.at3.services;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.config.FileStorageProperties;
import sit.int221.at3.entities.Event;
import sit.int221.at3.repositories.EventRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    private final Path fileStorageLocation;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public FileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException(
                "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String store(MultipartFile file, Integer id) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {// Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // If event is found then create event
            Event event = eventRepository.findById(id).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, id + " is not exist please find new id if exist.")
            );
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(id + ".jpg");
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (FileSizeLimitExceededException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size is higher than 10 MB. Please upload file is lower than 10 MB", ex);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(Integer id) {
        try {
            Path filePath = this.fileStorageLocation.resolve(id + ".jpg").normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"File not found");
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,"File not found", ex);
        }
    }

    public void deleteFileAsResource(Integer id) {
        try {
            Path filePath = this.fileStorageLocation.resolve(id + ".jpg").normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                resource.getFile().delete();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
            }
        } catch (MalformedURLException ex) {
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED,"File not found", ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}