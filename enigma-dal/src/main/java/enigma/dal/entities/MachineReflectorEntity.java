package enigma.dal.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "machines_reflectors")
public class MachineReflectorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "reflector_id", nullable = false, columnDefinition = "reflector_id_enum")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private ReflectorIdEnum reflectorId;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    private String output;

    public MachineReflectorEntity() {}

    public MachineReflectorEntity(MachineEntity machine, ReflectorIdEnum reflectorId, String input, String output) {
        this.machine = machine;
        this.reflectorId = reflectorId;
        this.input = input;
        this.output = output;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public MachineEntity getMachine() { return machine; }
    public void setMachine(MachineEntity machine) { this.machine = machine; }
    public ReflectorIdEnum getReflectorId() { return reflectorId; }
    public void setReflectorId(ReflectorIdEnum reflectorId) { this.reflectorId = reflectorId; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
}
