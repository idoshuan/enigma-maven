package enigma.api.services;

import enigma.api.dtos.ProcessResponse;
import enigma.api.utils.CompactCodeFormatter;
import enigma.dal.entities.ProcessingEntity;
import enigma.dal.repositories.MachineRepository;
import enigma.dal.repositories.ProcessingRepository;
import enigma.engine.core.Engine;
import enigma.engine.dtos.CodeDetails;
import enigma.engine.dtos.EngineDetails;
import enigma.sessions.SessionManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProcessService {

    private final SessionManager sessionManager;
    private final CompactCodeFormatter compactCodeFormatter;
    private final ProcessingRepository processingRepository;
    private final MachineRepository machineRepository;

    public ProcessService(SessionManager sessionManager, CompactCodeFormatter compactCodeFormatter,
                          ProcessingRepository processingRepository, MachineRepository machineRepository) {
        this.sessionManager = sessionManager;
        this.compactCodeFormatter = compactCodeFormatter;
        this.processingRepository = processingRepository;
        this.machineRepository = machineRepository;
    }

    public ProcessResponse process(String sessionID, String sessionId, String input) {
        String resolvedSessionId = sessionID != null ? sessionID : sessionId;

        SessionManager.SessionContext context = sessionManager.getSession(resolvedSessionId);
        Engine engine = context.engine();

        EngineDetails detailsBefore = engine.getEngineDetails();
        String codeCompact = detailsBefore.currentCode()
                .map(compactCodeFormatter::formatOriginalCodeCompact)
                .orElse("");

        long startTime = System.nanoTime();
        String output = engine.process(input);
        long durationNanos = System.nanoTime() - startTime;
        long duration = durationNanos / 1_000_000; // convert to milliseconds

        EngineDetails detailsAfter = engine.getEngineDetails();
        String currentPositionCompact = detailsAfter.currentCode()
                .map(compactCodeFormatter::formatCurrentPositionCompact)
                .orElse("");

        // Persist to DB
        UUID machineId = machineRepository.findByName(context.machineName())
                .map(m -> m.getId())
                .orElse(null);

        if (machineId != null) {
            ProcessingEntity entity = new ProcessingEntity(
                    machineId, resolvedSessionId, codeCompact, input, output, duration
            );
            processingRepository.save(entity);
        }

        return new ProcessResponse(output, currentPositionCompact);
    }
}
