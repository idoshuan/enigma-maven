package enigma.api.controllers;

import enigma.api.dtos.HistoryEntry;
import enigma.api.services.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enigma")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, List<HistoryEntry>>> getHistory(
            @RequestParam(value = "sessionID", required = false) String sessionId,
            @RequestParam(value = "machineName", required = false) String machineName) {
        Map<String, List<HistoryEntry>> history = historyService.getHistory(sessionId, machineName);
        return ResponseEntity.ok(history);
    }
}
