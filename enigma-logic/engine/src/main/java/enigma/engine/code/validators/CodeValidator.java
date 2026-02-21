package enigma.engine.code.validators;

import enigma.core.Inventory;
import enigma.engine.dtos.MachineCode;
import enigma.engine.exceptions.setup.*;

import java.util.HashSet;
import java.util.Set;

public class CodeValidator {
    private final Inventory inventory;

    public CodeValidator(Inventory inventory) {
        this.inventory = inventory;
    }

    public void validate(MachineCode setup) {
        validateRotorCount(setup);
        validateNoDuplicateRotors(setup);
        validateRotorIdsExist(setup);
        validatePositionCount(setup);
        validatePositionRange(setup);
        validateReflectorIdExists(setup);
        validatePlugboard(setup);
    }

    private void validateRotorCount(MachineCode setup) {
        int required = inventory.requiredRotorCount();
        if (setup.rotorIds().size() != required) {
            throw new InvalidRotorCountException(required, setup.rotorIds().size());
        }
    }

    private void validateNoDuplicateRotors(MachineCode setup) {
        Set<Integer> seen = new HashSet<>();
        for (int rotorId : setup.rotorIds()) {
            if (!seen.add(rotorId)) {
                throw new DuplicateRotorException(rotorId);
            }
        }
    }

    private void validateRotorIdsExist(MachineCode setup) {
        for (int rotorId : setup.rotorIds()) {
            if (!inventory.getRotorIds().contains(rotorId)) {
                throw new RotorNotFoundException(rotorId);
            }
        }
    }

    private void validatePositionCount(MachineCode setup) {
        if (setup.positions().size() != setup.rotorIds().size()) {
            throw new PositionCountMismatchException(setup.rotorIds().size(), setup.positions().size());
        }
    }

    private void validatePositionRange(MachineCode setup) {
        for (char position : setup.positions()) {
            if (!inventory.alphabet().contains(position)) {
                throw new InvalidPositionException(position, inventory.alphabet().toString());
            }
        }
    }

    private void validateReflectorIdExists(MachineCode setup) {
        if (!inventory.getReflectorIds().contains(setup.reflectorId())) {
            throw new ReflectorNotFoundException(setup.reflectorId());
        }
    }

private void validatePlugboard(MachineCode setup) {
    String plugboardPairs = setup.plugboardPairs();

    if (plugboardPairs == null || plugboardPairs.isEmpty()) {
        return;
    }

    if (plugboardPairs.length() % 2 != 0) {
        throw new PlugboardOddLengthException(plugboardPairs.length());
    }

    Set<Character> usedCharacters = new HashSet<>();

    for (int i = 0; i < plugboardPairs.length(); i += 2) {
        char first = plugboardPairs.charAt(i);
        char second = plugboardPairs.charAt(i + 1);
        
        validatePlugboardPair(first, second, usedCharacters);
    }
}

private void validatePlugboardPair(char first, char second, Set<Character> usedCharacters) {
    validateCharacterInAlphabet(first);
    validateCharacterInAlphabet(second);
    
    if (first == second) {
        throw new PlugboardSelfPairException(first);
    }
    
    if (!usedCharacters.add(first)) {
        throw new PlugboardDuplicateCharacterException(first);
    }
    if (!usedCharacters.add(second)) {
        throw new PlugboardDuplicateCharacterException(second);
    }
}

private void validateCharacterInAlphabet(char character) {
    if (!inventory.alphabet().contains(character)) {
        throw new PlugboardInvalidCharacterException(character, inventory.alphabet().getCharacters());
    }
}
}
