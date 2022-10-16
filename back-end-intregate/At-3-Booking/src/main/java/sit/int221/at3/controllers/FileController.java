package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.entities.Event;
import sit.int221.at3.services.EventService;
import sit.int221.at3.services.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("events/{id}")
    @ResponseBody
    public ResponseEntity<Resource> readFile(@PathVariable Integer id) throws IOException {
        Resource file = fileService.loadFileAsResource(id);
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(file);
    }

    @PostMapping("events/{id}")
    public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable Integer id) throws IOException {
        fileService.store(file,id);
        return "You successfully uploaded " + file.getOriginalFilename() + "!";
    }

    @DeleteMapping("events/{id}")
    @ResponseBody
    public String deleteFile(@PathVariable Integer id) {
        Resource file = fileService.loadFileAsResource(id);
        fileService.deleteFileAsResource(id);
        return "File " + file.getFilename() + " has been delete.";
    }
}