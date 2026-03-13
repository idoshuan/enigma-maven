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
        return ResponseEntity.ok(configurationService.configureManually(request));
    }

    @PutMapping(value = "/automatic", produces = "text/plain")
    public ResponseEntity<String> configureAutomatically(@RequestParam("sessionID") String sessionId) {
        return ResponseEntity.ok(configurationService.configureAutomatically(sessionId));
    }

    @PutMapping(value = "/reset", produces = "text/plain")
    public ResponseEntity<String> resetConfiguration(@RequestParam("sessionID") String sessionId) {
        return ResponseEntity.ok(configurationService.resetConfiguration(sessionId));
    }
}
