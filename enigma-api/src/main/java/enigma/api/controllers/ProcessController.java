package enigma.api.controllers;

import enigma.api.dtos.ProcessResponse;
import enigma.api.services.ProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enigma")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessResponse> process(
            @RequestParam("input") String input,
            @RequestParam("sessionID") String sessionId) {
        ProcessResponse response = processService.process(sessionId, input);
        return ResponseEntity.ok(response);
    }
}
