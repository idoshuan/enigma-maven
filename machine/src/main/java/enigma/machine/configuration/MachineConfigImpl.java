package enigma.machine.configuration;

import enigma.core.reflector.Reflector;
import enigma.core.rotor.Rotor;
import enigma.machine.plugboard.Plugboard;
import enigma.machine.plugboard.PlugboardImpl;

import java.util.ArrayList;
import java.util.List;

public class MachineConfigImpl implements MachineConfig {

    private final List<MountedRotor> rotors;
    private final Reflector reflector;
    private final Plugboard plugboard;

    public MachineConfigImpl(List<Rotor> selectedRotors,
                             List<Integer> positions,
                             Reflector reflector,
                             Plugboard plugboard
    ) {
        this.rotors = new ArrayList<>();
        this.reflector = reflector;
        this.plugboard = plugboard != null ? plugboard : PlugboardImpl.identity(26);

        for (int i = 0; i < selectedRotors.size(); i++) {
            rotors.add(new MountedRotor(selectedRotors.get(i), positions.get(i)));
        }
    }

    @Override
    public List<MountedRotor> getRotors() {
        return rotors;
    }

    @Override
    public Reflector getReflector() {
        return reflector;
    }

    @Override
    public Plugboard getPlugboard() {
        return plugboard;
    }

    @Override
    public List<Character> getCurrentPositionsAsChars() {
        return rotors.stream()
                .map(MountedRotor::getPositionAsChar)
                .toList();
    }
}
