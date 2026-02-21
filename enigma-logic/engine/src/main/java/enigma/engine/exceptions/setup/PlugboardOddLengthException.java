package enigma.engine.exceptions.setup;

public class PlugboardOddLengthException extends SetupException {
    public PlugboardOddLengthException(int length) {
        super("Plugboard input must have even length, but got " + length + " characters");
    }
}

