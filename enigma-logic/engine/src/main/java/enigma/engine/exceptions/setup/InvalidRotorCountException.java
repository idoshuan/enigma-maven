package enigma.engine.exceptions.setup;

public class InvalidRotorCountException extends SetupException {
    public InvalidRotorCountException(int required, int provided) {
        super("Exactly " + required + " rotors required, but " + provided + " provided");
    }
}

