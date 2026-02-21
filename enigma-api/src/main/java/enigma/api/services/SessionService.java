package enigma.api.services;

import enigma.api.dtos.SessionResponse;
import enigma.sessions.SessionManager;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final SessionManager sessionManager;

    public SessionService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SessionResponse createSession(String machineName) {
        String sessionId = sessionManager.createSession(machineName);
        return new SessionResponse(sessionId);
    }

    public void deleteSession(String sessionId) {
        sessionManager.deleteSession(sessionId);
    }
}
