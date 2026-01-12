package enigma.engine.exceptions.setup;

public class PlugboardInvalidCharacterException extends SetupException {
    public PlugboardInvalidCharacterException(char character, String validCharacters) {
        super("Character '" + character + "' is not in the machine alphabet. Valid characters: " + validCharacters);
    }
}

