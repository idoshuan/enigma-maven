package enigma.engine.exceptions.setup;

public class PlugboardDuplicateCharacterException extends SetupException {
    public PlugboardDuplicateCharacterException(char character) {
        super("Character '" + character + "' appears more than once in plugboard configuration");
    }
}

