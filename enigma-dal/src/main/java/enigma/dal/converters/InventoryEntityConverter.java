package enigma.dal.converters;

import enigma.core.Inventory;
import enigma.core.alphabet.Alphabet;
import enigma.core.alphabet.AlphabetImpl;
import enigma.core.reflector.Reflector;
import enigma.core.reflector.ReflectorImpl;
import enigma.core.rotor.Rotor;
import enigma.core.rotor.RotorImpl;
import enigma.dal.entities.*;

import java.util.*;
import java.util.stream.Collectors;

public class InventoryEntityConverter {

    public MachineEntity toMachineEntity(Inventory inventory) {
        MachineEntity machineEntity = new MachineEntity(
                inventory.name(),
                inventory.requiredRotorCount(),
                inventory.alphabet().getCharacters()
        );

        List<MachineRotorEntity> rotorEntities = new ArrayList<>();
        for (Map.Entry<Integer, Rotor> entry : inventory.rotors().entrySet()) {
            int rotorId = entry.getKey();
            Rotor rotor = entry.getValue();

            String rightColumn = buildRightColumn(rotor, inventory.alphabet().size());
            String leftColumn = buildLeftColumn(rotor, rightColumn, inventory.alphabet());

            rotorEntities.add(new MachineRotorEntity(
                    machineEntity,
                    rotorId,
                    rotor.getNotchIndex() + 1,
                    rightColumn,
                    leftColumn
            ));
        }
        machineEntity.setRotors(rotorEntities);

        List<MachineReflectorEntity> reflectorEntities = new ArrayList<>();
        for (Map.Entry<Integer, Reflector> entry : inventory.reflectors().entrySet()) {
            int reflectorId = entry.getKey();
            Reflector reflector = entry.getValue();
            ReflectorIdEnum idEnum = ReflectorIdEnum.values()[reflectorId - 1];

            StringBuilder inputStr = new StringBuilder();
            StringBuilder outputStr = new StringBuilder();

            for (int i = 0; i < inventory.alphabet().size(); i++) {
                int reflected = reflector.reflect(i);
                inputStr.append(i > 0 ? "," : "").append(i + 1);
                outputStr.append(i > 0 ? "," : "").append(reflected + 1);
            }

            reflectorEntities.add(new MachineReflectorEntity(
                    machineEntity, idEnum, inputStr.toString(), outputStr.toString()
            ));
        }
        machineEntity.setReflectors(reflectorEntities);

        return machineEntity;
    }

    public Inventory toInventory(MachineEntity machineEntity) {
        String abc = machineEntity.getAbc();
        Alphabet alphabet = new AlphabetImpl(abc);

        Map<Integer, Rotor> rotors = new HashMap<>();
        for (MachineRotorEntity re : machineEntity.getRotors()) {
            String rightStr = re.getWiringRight();
            String leftStr = re.getWiringLeft();

            Map<Integer, Integer> wiring = new HashMap<>();
            for (int i = 0; i < alphabet.size(); i++) {
                char letter = alphabet.toChar(i);
                int rightIndex = rightStr.indexOf(letter);
                int leftIndex = leftStr.indexOf(letter);
                wiring.put(rightIndex, leftIndex);
            }

            int notchIndex = re.getNotch() - 1;
            rotors.put(re.getRotorId(), new RotorImpl(wiring, notchIndex, rightStr));
        }

        Map<Integer, Reflector> reflectors = new HashMap<>();
        for (MachineReflectorEntity rfe : machineEntity.getReflectors()) {
            int reflectorId = rfe.getReflectorId().ordinal() + 1;

            int[] inputs = Arrays.stream(rfe.getInput().split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim()) - 1)
                    .toArray();
            int[] outputs = Arrays.stream(rfe.getOutput().split(","))
                    .mapToInt(s -> Integer.parseInt(s.trim()) - 1)
                    .toArray();

            Map<Integer, Integer> wiring = new HashMap<>();
            for (int i = 0; i < inputs.length; i++) {
                wiring.put(inputs[i], outputs[i]);
            }

            reflectors.put(reflectorId, new ReflectorImpl(wiring));
        }

        return new Inventory(machineEntity.getName(), alphabet, rotors, reflectors, machineEntity.getRotorsCount());
    }

    private String buildRightColumn(Rotor rotor, int size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(rotor.positionToChar(i));
        }
        return sb.toString();
    }

    private String buildLeftColumn(Rotor rotor, String rightColumn, Alphabet alphabet) {
        char[] leftColumn = new char[alphabet.size()];
        for (int p = 0; p < alphabet.size(); p++) {
            int q = rotor.process(p, enigma.core.rotor.Direction.FORWARD);
            leftColumn[q] = rightColumn.charAt(p);
        }
        return new String(leftColumn);
    }
}
