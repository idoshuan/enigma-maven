package enigma.sessions.exceptions;

public class MachineNotFoundException extends RuntimeException {
    public MachineNotFoundException(String machineName) {
        super("Machine not found: " + machineName);
    }
}
