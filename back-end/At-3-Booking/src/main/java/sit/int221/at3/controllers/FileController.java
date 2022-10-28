package sit.int221.at3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int221.at3.repositories.EventRepository;
import sit.int221.at3.services.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("events/{id}/{fileName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> readFile(HttpServletResponse response, @PathVariable Integer id, @PathVariable String fileName) throws IOException {
        Resource file = fileService.loadFileAsResource(id, fileName);
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + fileName + "\""));
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
    }

    @PostMapping("events/{id}")
    public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable Integer id) throws IOException {
        fileService.store(file,id);
        return "You successfully uploaded " + file.getOriginalFilename() + "!";
    }

    @DeleteMapping("events/{id}")
    @ResponseBody
    public String deleteFile(@PathVariable Integer id) {
        fileService.deleteFileAsResource(id);
        return "File has been delete.";
    }
}