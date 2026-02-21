package enigma.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConfigResponse(
        int totalRotors,
        int totalReflectors,
        int totalProcessedMessages,
        String originalCodeCompact,
        String currentRotorsPositionCompact,
        EnigmaCodeStructure originalCode,
        EnigmaCodeStructure currentRotorsPosition
) {}
