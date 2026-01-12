package enigma.console.menu;

import enigma.console.core.EngineState;

public record MenuItem(String label, EngineState requiredEngineState, MenuAction action) {
    
    public boolean isAvailableIn(EngineState currentState) {
        return requiredEngineState.isAvailableIn(currentState);
    }
    
    public String getUnavailableReason(EngineState currentState) {
        return switch (requiredEngineState) {
            case INITIALIZED -> "This operation requires a loaded machine configuration. Please load a configuration file first.";
            case CONFIGURED -> currentState == EngineState.UNINITIALIZED 
                ? "This operation requires a configured machine. Please load a configuration file and configure the machine first."
                : "This operation requires a configured machine. Please configure the machine first.";
            case UNINITIALIZED -> "This operation is not available in the current state.";
        };
    }
}