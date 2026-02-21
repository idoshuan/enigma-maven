package enigma.sessions;

import enigma.core.Inventory;
import enigma.sessions.exceptions.MachineAlreadyExistsException;
import enigma.sessions.exceptions.MachineNotFoundException;

import java.util.concurrent.ConcurrentHashMap;

public class MachineRegistry {

    private final ConcurrentHashMap<String, Inventory> machines = new ConcurrentHashMap<>();

    public void register(String name, Inventory inventory) {
        if (machines.containsKey(name)) {
            throw new MachineAlreadyExistsException(name);
        }
        machines.put(name, inventory);
    }

    public Inventory get(String name) {
        Inventory inventory = machines.get(name);
        if (inventory == null) {
            throw new MachineNotFoundException(name);
        }
        return inventory;
    }

    public boolean exists(String name) {
        return machines.containsKey(name);
    }
}
