package enigma.engine.exceptions.setup;

public class PlugboardSelfPairException extends SetupException {
    public PlugboardSelfPairException(char character) {
        super("Character '" + character + "' cannot be paired with itself");
    }
}

