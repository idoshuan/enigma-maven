package enigma.loader.core.exceptions.rotor;

public class InvalidRotorCountException extends RotorException {
    public InvalidRotorCountException(int count) {
        super("Rotors count must be positive, but was: " + count);
    }
}

