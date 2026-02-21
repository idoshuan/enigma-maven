package enigma.dal.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "processing")
public class ProcessingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "machine_id", nullable = false)
    private UUID machineId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    private String output;

    @Column(nullable = false)
    private long time;

    public ProcessingEntity() {}

    public ProcessingEntity(UUID machineId, String sessionId, String code, String input, String output, long time) {
        this.machineId = machineId;
        this.sessionId = sessionId;
        this.code = code;
        this.input = input;
        this.output = output;
        this.time = time;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }
}
