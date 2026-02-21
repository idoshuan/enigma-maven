package enigma.sessions.exceptions;

public class MachineAlreadyExistsException extends RuntimeException {
    public MachineAlreadyExistsException(String machineName) {
        super("Machine already exists: " + machineName);
    }
}
