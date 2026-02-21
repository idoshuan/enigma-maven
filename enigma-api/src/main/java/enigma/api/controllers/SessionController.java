package enigma.api.controllers;

import enigma.api.dtos.CreateSessionRequest;
import enigma.api.dtos.SessionResponse;
import enigma.api.services.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enigma")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/session")
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request) {
        SessionResponse response = sessionService.createSession(request.machine());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/session")
    public ResponseEntity<Void> deleteSession(@RequestParam("sessionID") String sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
