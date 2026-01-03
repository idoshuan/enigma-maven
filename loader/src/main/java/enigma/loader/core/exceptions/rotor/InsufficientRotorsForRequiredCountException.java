package enigma.loader.core.exceptions.rotor;

public class InsufficientRotorsForRequiredCountException extends RotorException {
    public InsufficientRotorsForRequiredCountException(int requiredCount, int availableCount) {
        super("Machine requires " + requiredCount + " rotors, but only " + availableCount + " rotors defined in XML");
    }
}

