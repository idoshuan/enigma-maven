package enigma.api.dtos;

import java.util.List;

public record ManualConfigRequest(
        String sessionID,
        List<RotorConfigDto> rotors,
        String reflector,
        List<PlugDto> plugs
) {}
