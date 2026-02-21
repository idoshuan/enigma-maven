package enigma.api.utils;

import enigma.engine.dtos.CodeDetails;
import enigma.loader.core.utils.RomanNumeral;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompactCodeFormatter {

    public String formatOriginalCodeCompact(CodeDetails code) {
        List<Integer> reversedRotorIds = code.rotorIds().reversed();
        List<Character> reversedPositions = code.positions().reversed();
        List<Integer> reversedNotchDistances = code.notchDistances().reversed();

        String rotorIdsStr = reversedRotorIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String positionsStr = IntStream.range(0, reversedPositions.size())
                .mapToObj(i -> reversedPositions.get(i) + "(" + reversedNotchDistances.get(i) + ")")
                .collect(Collectors.joining(","));

        String reflectorStr = RomanNumeral.fromInt(code.reflectorId()).name();

        StringBuilder result = new StringBuilder();
        result.append("<").append(rotorIdsStr).append(">");
        result.append("<").append(positionsStr).append(">");
        result.append("<").append(reflectorStr).append(">");

        if (code.hasPlugboardPairs()) {
            result.append("<").append(formatPlugboardPairs(code.plugboardPairs())).append(">");
        }

        return result.toString();
    }

    public String formatCurrentPositionCompact(CodeDetails code) {
        List<Character> reversedPositions = code.positions().reversed();
        List<Integer> reversedNotchDistances = code.notchDistances().reversed();

        return IntStream.range(0, reversedPositions.size())
                .mapToObj(i -> reversedPositions.get(i) + "(" + reversedNotchDistances.get(i) + ")")
                .collect(Collectors.joining(","));
    }

    private String formatPlugboardPairs(String plugboardPairs) {
        if (plugboardPairs == null || plugboardPairs.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plugboardPairs.length(); i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(plugboardPairs.charAt(i));
            sb.append("|");
            sb.append(plugboardPairs.charAt(i + 1));
        }
        return sb.toString();
    }
}
