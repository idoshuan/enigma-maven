package enigma.api.services;

import enigma.api.dtos.*;
import enigma.api.utils.CompactCodeFormatter;
import enigma.engine.core.Engine;
import enigma.engine.dtos.CodeDetails;
import enigma.engine.dtos.EngineDetails;
import enigma.engine.dtos.MachineCode;
import enigma.loader.core.utils.RomanNumeral;
import enigma.sessions.SessionManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
public class ConfigurationService {

    private final SessionManager sessionManager;
    private final CompactCodeFormatter compactCodeFormatter;

    public ConfigurationService(SessionManager sessionManager, CompactCodeFormatter compactCodeFormatter) {
        this.sessionManager = sessionManager;
        this.compactCodeFormatter = compactCodeFormatter;
    }

    public ConfigResponse getConfig(String sessionId, boolean verbose) {
        Engine engine = sessionManager.getSession(sessionId).engine();
        EngineDetails details = engine.getEngineDetails();

        String originalCompact = details.initialCode()
                .map(compactCodeFormatter::formatOriginalCodeCompact)
                .orElse(null);

        String currentCompact = details.currentCode()
                .map(compactCodeFormatter::formatCurrentPositionCompact)
                .orElse(null);

        EnigmaCodeStructure originalStructure = null;
        EnigmaCodeStructure currentStructure = null;

        if (verbose) {
            originalStructure = details.initialCode()
                    .map(this::toCodeStructure)
                    .orElse(null);
            currentStructure = details.currentCode()
                    .map(this::toCodeStructure)
                    .orElse(null);
        }

        return new ConfigResponse(
                details.rotorCount(),
                details.reflectorCount(),
                details.totalMessagesProcessed(),
                originalCompact,
                currentCompact,
                originalStructure,
                currentStructure
        );
    }

    public void configureManually(ManualConfigRequest request) {
        Engine engine = sessionManager.getSession(request.sessionID()).engine();

        // API sends rotors L→R, engine expects R→L — reverse
        List<RotorConfigDto> reversedRotors = new ArrayList<>(request.rotors());
        Collections.reverse(reversedRotors);

        List<Integer> rotorIds = reversedRotors.stream()
                .map(RotorConfigDto::rotorNumber)
                .toList();

        List<Character> positions = reversedRotors.stream()
                .map(r -> r.rotorPosition().toUpperCase().charAt(0))
                .toList();

        int reflectorId = RomanNumeral.fromString(request.reflector())
                .map(RomanNumeral::toInt)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reflector: " + request.reflector()));

        String plugboardPairs = "";
        if (request.plugs() != null && !request.plugs().isEmpty()) {
            StringBuilder pb = new StringBuilder();
            for (PlugDto plug : request.plugs()) {
                pb.append(plug.plug1().toUpperCase());
                pb.append(plug.plug2().toUpperCase());
            }
            plugboardPairs = pb.toString();
        }

        MachineCode code = new MachineCode(rotorIds, positions, reflectorId, plugboardPairs);
        engine.configureMachineManually(code);
    }

    public void configureAutomatically(String sessionId) {
        Engine engine = sessionManager.getSession(sessionId).engine();
        engine.configureMachineRandomly();
    }

    public void resetConfiguration(String sessionId) {
        Engine engine = sessionManager.getSession(sessionId).engine();
        engine.resetConfiguration();
    }

    private EnigmaCodeStructure toCodeStructure(CodeDetails code) {
        List<Integer> reversedRotorIds = code.rotorIds().reversed();
        List<Character> reversedPositions = code.positions().reversed();
        List<Integer> reversedNotchDistances = code.notchDistances().reversed();

        List<EnigmaCodeStructure.RotorDetailDto> rotorDetails = IntStream.range(0, reversedRotorIds.size())
                .mapToObj(i -> new EnigmaCodeStructure.RotorDetailDto(
                        reversedRotorIds.get(i),
                        String.valueOf(reversedPositions.get(i)),
                        reversedNotchDistances.get(i)
                ))
                .toList();

        String reflector = RomanNumeral.fromInt(code.reflectorId()).name();

        List<PlugDto> plugs = new ArrayList<>();
        String pp = code.plugboardPairs();
        if (pp != null && !pp.isEmpty()) {
            for (int i = 0; i < pp.length(); i += 2) {
                plugs.add(new PlugDto(String.valueOf(pp.charAt(i)), String.valueOf(pp.charAt(i + 1))));
            }
        }

        return new EnigmaCodeStructure(rotorDetails, reflector, plugs);
    }
}
