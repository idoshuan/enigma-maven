package enigma.sessions;

import enigma.core.Inventory;
import enigma.engine.core.Engine;
import enigma.engine.core.EngineImpl;
import enigma.sessions.exceptions.SessionNotFoundException;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final MachineRegistry machineRegistry;
    private final ConcurrentHashMap<String, SessionContext> sessions = new ConcurrentHashMap<>();

    public record SessionContext(String sessionId, String machineName, Engine engine) {}

    public SessionManager(MachineRegistry machineRegistry) {
        this.machineRegistry = machineRegistry;
    }

    public String createSession(String machineName) {
        Inventory inventory = machineRegistry.get(machineName);
        String sessionId = UUID.randomUUID().toString();

        EngineImpl engine = new EngineImpl();
        engine.loadFromInventory(inventory);

        sessions.put(sessionId, new SessionContext(sessionId, machineName, engine));
        return sessionId;
    }

    public SessionContext getSession(String sessionId) {
        SessionContext context = sessions.get(sessionId);
        if (context == null) {
            throw new SessionNotFoundException(sessionId);
        }
        return context;
    }

    public void deleteSession(String sessionId) {
        if (sessions.remove(sessionId) == null) {
            throw new SessionNotFoundException(sessionId);
        }
    }
}
