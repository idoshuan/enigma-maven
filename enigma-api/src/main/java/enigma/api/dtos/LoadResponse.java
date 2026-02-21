package enigma.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoadResponse(boolean success, String name, String error) {
    public static LoadResponse success(String name) {
        return new LoadResponse(true, name, null);
    }

    public static LoadResponse failure(String error) {
        return new LoadResponse(false, null, error);
    }
}
