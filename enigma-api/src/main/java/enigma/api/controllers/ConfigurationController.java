package enigma.api.controllers;

import enigma.api.dtos.ConfigResponse;
import enigma.api.dtos.ManualConfigRequest;
import enigma.api.services.ConfigurationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enigma/config")
public class ConfigurationController {

    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public ResponseEntity<ConfigResponse> getConfig(
            @RequestParam("sessionID") String sessionId,
            @RequestParam(value = "verbose", defaultValue = "false") boolean verbose) {
        ConfigResponse response = configurationService.getConfig(sessionId, verbose);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/manual", produces = "text/plain")
    public ResponseEntity<String> configureManually(@RequestBody ManualConfigRequest request) {
        configurationService.configureManually(request);
        return ResponseEntity.ok("Manual code set successfully");
    }

    @PutMapping(value = "/automatic", produces = "text/plain")
    public ResponseEntity<String> configureAutomatically(@RequestParam("sessionID") String sessionId) {
        configurationService.configureAutomatically(sessionId);
        return ResponseEntity.ok("Automatic code setup completed successfully");
    }

    @PutMapping(value = "/reset", produces = "text/plain")
    public ResponseEntity<String> resetConfiguration(@RequestParam("sessionID") String sessionId) {
        configurationService.resetConfiguration(sessionId);
        return ResponseEntity.ok("Automatic code setup completed successfully");
    }
}
