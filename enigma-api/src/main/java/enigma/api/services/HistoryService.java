package enigma.api.services;

import enigma.api.dtos.HistoryEntry;
import enigma.dal.entities.ProcessingEntity;
import enigma.dal.repositories.MachineRepository;
import enigma.dal.repositories.ProcessingRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final ProcessingRepository processingRepository;
    private final MachineRepository machineRepository;

    public HistoryService(ProcessingRepository processingRepository, MachineRepository machineRepository) {
        this.processingRepository = processingRepository;
        this.machineRepository = machineRepository;
    }

    public Map<String, List<HistoryEntry>> getHistory(String sessionId, String machineName) {
        boolean hasSession = sessionId != null && !sessionId.isBlank();
        boolean hasMachine = machineName != null && !machineName.isBlank();

        if (hasSession == hasMachine) {
            throw new IllegalArgumentException("Provide exactly one of sessionID or machineName");
        }

        List<ProcessingEntity> entities;

        if (hasSession) {
            entities = processingRepository.findBySessionId(sessionId);
        } else {
            UUID machineId = machineRepository.findByName(machineName)
                    .orElseThrow(() -> new IllegalArgumentException("Machine not found: " + machineName))
                    .getId();
            entities = processingRepository.findByMachineId(machineId);
        }

        return entities.stream()
                .collect(Collectors.groupingBy(
                        ProcessingEntity::getCode,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                e -> new HistoryEntry(e.getInput(), e.getOutput(), e.getTime()),
                                Collectors.toList()
                        )
                ));
    }
}
