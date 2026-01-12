package enigma.machine.configuration;

import enigma.core.reflector.Reflector;
import enigma.machine.plugboard.Plugboard;

import java.util.List;

public interface MachineConfig {
    
    List<MountedRotor> getRotors();
    Reflector getReflector();
    Plugboard getPlugboard();
    List<Character> getCurrentPositionsAsChars();
}
