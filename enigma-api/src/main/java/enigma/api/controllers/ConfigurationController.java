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

    @PutMapping("/manual")
    public ResponseEntity<String> configureManually(@RequestBody ManualConfigRequest request) {
        configurationService.configureManually(request);
        return ResponseEntity.ok("Machine configured manually successfully");
    }

    @PutMapping("/automatic")
    public ResponseEntity<String> configureAutomatically(@RequestParam("sessionID") String sessionId) {
        configurationService.configureAutomatically(sessionId);
        return ResponseEntity.ok("Machine configured automatically successfully");
    }

    @PutMapping("/reset")
    public ResponseEntity<String> resetConfiguration(@RequestParam("sessionID") String sessionId) {
        configurationService.resetConfiguration(sessionId);
        return ResponseEntity.ok("Machine configuration reset successfully");
    }
}
