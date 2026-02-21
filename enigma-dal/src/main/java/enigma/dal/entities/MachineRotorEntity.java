package enigma.dal.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "machines_rotors")
public class MachineRotorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachineEntity machine;

    @Column(name = "rotor_id", nullable = false)
    private int rotorId;

    @Column(nullable = false)
    private int notch;

    @Column(name = "wiring_right", nullable = false)
    private String wiringRight;

    @Column(name = "wiring_left", nullable = false)
    private String wiringLeft;

    public MachineRotorEntity() {}

    public MachineRotorEntity(MachineEntity machine, int rotorId, int notch, String wiringRight, String wiringLeft) {
        this.machine = machine;
        this.rotorId = rotorId;
        this.notch = notch;
        this.wiringRight = wiringRight;
        this.wiringLeft = wiringLeft;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public MachineEntity getMachine() { return machine; }
    public void setMachine(MachineEntity machine) { this.machine = machine; }
    public int getRotorId() { return rotorId; }
    public void setRotorId(int rotorId) { this.rotorId = rotorId; }
    public int getNotch() { return notch; }
    public void setNotch(int notch) { this.notch = notch; }
    public String getWiringRight() { return wiringRight; }
    public void setWiringRight(String wiringRight) { this.wiringRight = wiringRight; }
    public String getWiringLeft() { return wiringLeft; }
    public void setWiringLeft(String wiringLeft) { this.wiringLeft = wiringLeft; }
}
