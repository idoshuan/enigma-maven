package enigma.console.core;

import enigma.console.exceptions.ConsoleException;
import enigma.console.io.ConfigurationInputParser;
import enigma.console.io.ConsoleDisplay;
import enigma.console.io.ConsoleInputHandler;
import enigma.console.menu.Menu;
import enigma.console.menu.MenuImpl;
import enigma.console.menu.MenuItem;
import enigma.engine.core.Engine;
import enigma.engine.dtos.EngineDetails;
import enigma.engine.dtos.MachineCode;
import enigma.engine.dtos.SessionRecord;
import enigma.engine.exceptions.EngineException;
import enigma.loader.core.exceptions.LoaderException;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ConsoleImpl implements Console {

    private boolean isRunning;
    private final Engine engine;
    private final ConsoleStateManager stateManager;
    private final ConsoleInputHandler inputHandler;
    private final ConsoleDisplay display;
    private final ConfigurationInputParser parser;
    private final Menu menu;

    public ConsoleImpl(Engine engine, 
                      ConsoleStateManager stateManager,
                      ConsoleInputHandler inputHandler,
                      ConsoleDisplay display,
                      ConfigurationInputParser parser) {
        this.engine = engine;
        this.stateManager = stateManager;
        this.inputHandler = inputHandler;
        this.display = display;
        this.parser = parser;
        this.menu = configureMenu();
        this.isRunning = false;
    }

    @Override
    public void run() {
        isRunning = true;

        while (isRunning) {
            try {
                List<MenuItem> allItems = menu.getAllItems();
                display.showMenu(menu.getTitle(), allItems);
                display.showPrompt("\nEnter your choice: ");
                int choice = inputHandler.readChoice(1, allItems.size());
                MenuItem selected = allItems.get(choice - 1);
                
                if (selected.isAvailableIn(stateManager.getCurrentState())) {
                    selected.action().execute();
                } else {
                    display.showError(selected.getUnavailableReason(stateManager.getCurrentState()));
                }
            } catch (ConsoleException e) {
                display.showError(e.getMessage());
            }
        }
    }

    private void loadConfiguration() {
        try {
            String filePath = inputHandler.readLine("Enter path to configuration file: ").trim();
            engine.loadFromFile(filePath);
            stateManager.transitionToInitialized();
            display.showSuccess("Configuration loaded successfully!");
        } catch (LoaderException e) {
            display.showError(e.getMessage());
        }
    }

    private void displaySpecifications() {
        EngineDetails engineDetails = engine.getEngineDetails();
        display.showEngineDetails(engineDetails);
    }

    private void configureManually() {
        try {
            displayAvailableComponents();
            
            List<Integer> rotorIds = collectRotorIds();
            List<Character> positions = collectPositions();
            int reflectorId = collectReflectorId();
            String plugboardPairs = collectPlugboardPairs();

            Collections.reverse(rotorIds);
            Collections.reverse(positions);
            
            MachineCode machineCode = new MachineCode(rotorIds, positions, reflectorId, plugboardPairs);
            engine.configureMachineManually(machineCode);
            
            stateManager.transitionToConfigured();
            display.showSuccess("Machine configured manually!");

            String plugboardDisplay = formatPlugboardForDisplay(plugboardPairs);
            display.showInfo("Configuration: Rotors (L->R)=" + rotorIds + ", Positions (L->R)=" + positions + ", Reflector=" + reflectorId + ", Plugboard=" + plugboardDisplay);
        } catch (EngineException e) {
            display.showError(e.getMessage());
        } catch (NumberFormatException e) {
            display.showError("Invalid input format. Please enter numeric values.");
        } catch (Exception e) {
            display.showError("Configuration failed: " + e.getMessage());
        }
    }
    
    private void displayAvailableComponents() {
        Set<Integer> availableRotorIds = engine.getAvailableRotorIds();
        Set<Integer> availableReflectorIds = engine.getAvailableReflectorIds();
        int requiredRotors = engine.getRequiredRotorCount();
        
        display.showInfo("=== Manual Machine Configuration ===");
        display.showInfo("Available Rotors: " + availableRotorIds);
        display.showInfo("Available Reflectors: " + availableReflectorIds);
        display.showInfo("Note: You must select exactly " + requiredRotors + " rotors (no duplicates)\n");
    }
    
    private List<Integer> collectRotorIds() {
        display.showInfo("Enter rotor IDs separated by commas (e.g 1,2,3):");
        String input = inputHandler.readLine("Rotors: ");
        return parser.parseRotorIds(input);
    }
    
    private List<Character> collectPositions() {
        display.showInfo("\nEnter initial positions as a contiguous string (e.g., 4D8A):");
        display.showInfo("Note: Rightmost character = position of rightmost rotor");
        String input = inputHandler.readLine("Positions: ");
        return parser.parsePositions(input);
    }
    
    private int collectReflectorId() {
        display.showInfo("\nEnter reflector ID:");
        String input = inputHandler.readLine("Reflector: ");
        return parser.parseReflectorId(input);
    }

    private String collectPlugboardPairs() {
        display.showInfo("\nEnter plugboard pairs as a continuous string (e.g., ABXY for A-B and X-Y pairs):");
        display.showInfo("Note: Leave empty for no plugboard connections. Spaces and special characters are valid.");
        String input = inputHandler.readLine("Plugboard: ");
        return parser.parsePlugboard(input);
    }

    private String formatPlugboardForDisplay(String plugboardPairs) {
        if (plugboardPairs == null || plugboardPairs.isEmpty()) {
            return "None";
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < plugboardPairs.length(); i += 2) {
            if (i > 0) {
                result.append(", ");
            }
            result.append(plugboardPairs.charAt(i))
                  .append("-")
                  .append(plugboardPairs.charAt(i + 1));
        }
        return result.toString();
    }

    private void configureRandomly() {
        engine.configureMachineRandomly();
        stateManager.transitionToConfigured();
        display.showSuccess("Machine configured randomly!");
    }

    private void processInput() {
        try {
            String input = inputHandler.readLine("Enter text to process: ");
            String output = engine.process(input);
            display.showProcessResult(output);
        } catch (EngineException e) {
            display.showError(e.getMessage());
        }
    }

    private void resetMachine() {
        engine.resetConfiguration();
        display.showSuccess("Machine reset to initial configuration!");
    }

    private void displayStatistics() {
        List<SessionRecord> statistics = engine.getStatistics();
        display.showStatistics(statistics);
    }

    private void saveMachineState() {
        try {
            String filePath = inputHandler.readLine("Enter path to save machine state (without extension): ").trim();
            engine.saveState(filePath);
            display.showSuccess("Machine state saved successfully to " + filePath + ".enigma");
        } catch (EngineException e) {
            display.showError(e.getMessage());
        }
    }

    private void loadMachineState() {
        try {
            String filePath = inputHandler.readLine("Enter path to load machine state (without extension): ").trim();
            engine.loadState(filePath);
            stateManager.setState(EngineState.CONFIGURED);
            display.showSuccess("Machine state loaded successfully from " + filePath + ".enigma");
        } catch (EngineException e) {
            display.showError(e.getMessage());
        }
    }

    private void quit() {
        isRunning = false;
        display.showInfo("Goodbye!");
    }

    private Menu configureMenu() {
        Menu menu = new MenuImpl("Enigma Machine");

        menu.addItem(new MenuItem("Load Machine Details from XML File", EngineState.UNINITIALIZED, this::loadConfiguration));
        menu.addItem(new MenuItem("Get Current Machine Status", EngineState.INITIALIZED, this::displaySpecifications));
        menu.addItem(new MenuItem("Manual Code Setup", EngineState.INITIALIZED, this::configureManually));
        menu.addItem(new MenuItem("Random Code Setup", EngineState.INITIALIZED, this::configureRandomly));
        menu.addItem(new MenuItem("Process Input", EngineState.CONFIGURED, this::processInput));
        menu.addItem(new MenuItem("Reset to Original Code", EngineState.CONFIGURED, this::resetMachine));
        menu.addItem(new MenuItem("History", EngineState.INITIALIZED, this::displayStatistics));
        menu.addItem(new MenuItem("Exit", EngineState.UNINITIALIZED, this::quit));
        menu.addItem(new MenuItem("Load from File", EngineState.UNINITIALIZED, this::loadMachineState));
        menu.addItem(new MenuItem("Save to File", EngineState.CONFIGURED, this::saveMachineState));

        return menu;
    }
}
