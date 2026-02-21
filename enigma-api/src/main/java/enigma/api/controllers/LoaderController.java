package enigma.api.controllers;

import enigma.api.dtos.LoadResponse;
import enigma.api.services.LoaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/enigma")
public class LoaderController {

    private final LoaderService loaderService;

    public LoaderController(LoaderService loaderService) {
        this.loaderService = loaderService;
    }

    @PostMapping("/load")
    public ResponseEntity<LoadResponse> loadMachine(@RequestParam("file") MultipartFile file) {
        LoadResponse response = loaderService.loadMachine(file);
        return ResponseEntity.ok(response);
    }
}
