package enigma.machine.core;

import enigma.machine.configuration.MachineConfig;

public interface Machine {
    
    char process(char input);
    void setConfig(MachineConfig machineConfig);
    MachineConfig getConfig();
}
