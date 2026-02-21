package enigma.api.dtos;

import java.util.List;

public record EnigmaCodeStructure(
        List<RotorDetailDto> rotors,
        String reflector,
        List<PlugDto> plugs
) {
    public record RotorDetailDto(int rotorNumber, String rotorPosition, int notchDistance) {}
}
