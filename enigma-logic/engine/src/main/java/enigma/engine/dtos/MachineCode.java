package enigma.engine.dtos;

import java.io.Serializable;
import java.util.List;

public record MachineCode(
        List<Integer> rotorIds,
        List<Character> positions,
        int reflectorId,
        String plugboardPairs
) implements Serializable {

    public MachineCode {
        rotorIds = List.copyOf(rotorIds);
        positions = List.copyOf(positions);
        plugboardPairs = plugboardPairs != null ? plugboardPairs : "";
    }

    public MachineCode withPositions(List<Character> newPositions) {
        return new MachineCode(rotorIds, newPositions, reflectorId, plugboardPairs);
    }
}
