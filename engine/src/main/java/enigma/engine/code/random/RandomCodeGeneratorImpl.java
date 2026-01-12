package enigma.engine.code.random;

import enigma.core.Inventory;
import enigma.engine.dtos.MachineCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomCodeGeneratorImpl implements RandomCodeGenerator {

    private final Random random;
    private final Inventory inventory;

    public RandomCodeGeneratorImpl(Inventory inventory) {
        this(inventory, new Random());
    }

    public RandomCodeGeneratorImpl(Inventory inventory, Random random) {
        this.inventory = inventory;
        this.random = random;
    }

    @Override
    public MachineCode generate() {
        int numOfRotorsToUse = inventory.requiredRotorCount();

        List<Integer> rotorIds = getRandomRotorIds(numOfRotorsToUse);
        List<Character> positions = getRandomPositions(numOfRotorsToUse);
        int reflectorId = getRandomReflectorId();
        String plugboardPairs = getRandomPlugboardPairs();

        return new MachineCode(rotorIds, positions, reflectorId, plugboardPairs);
    }

    private List<Integer> getRandomRotorIds(int count) {
        List<Integer> availableIds = new ArrayList<>(inventory.getRotorIds());
        Collections.shuffle(availableIds, random);
        return availableIds.subList(0, count);
    }

    private List<Character> getRandomPositions(int count) {
        return random.ints(count, 0, inventory.alphabet().size())
                .mapToObj(inventory.alphabet()::toChar)
                .toList();
    }

    private int getRandomReflectorId() {
        List<Integer> availableIds = new ArrayList<>(inventory.getReflectorIds());
        return availableIds.get(random.nextInt(availableIds.size()));
    }

    private String getRandomPlugboardPairs() {
        int maxPairs = inventory.alphabet().size() / 2;
        int numPairs = selectRandomPairCount(maxPairs);

        if (numPairs == 0) {
            return "";
        }

        return buildPlugboardString(numPairs);
    }

    private int selectRandomPairCount(int maxPairs) {
        return (int) (random.nextDouble() * random.nextDouble() * (maxPairs + 1));
    }

    private String buildPlugboardString(int numPairs) {
        List<Character> shuffledCharacters = getShuffledAlphabetCharacters();

        StringBuilder plugboardPairs = new StringBuilder();
        for (int i = 0; i < numPairs * 2; i++) {
            plugboardPairs.append(shuffledCharacters.get(i));
        }

        return plugboardPairs.toString();
    }

    private List<Character> getShuffledAlphabetCharacters() {
        List<Character> characters = new ArrayList<>();
        for (int i = 0; i < inventory.alphabet().size(); i++) {
            characters.add(inventory.alphabet().toChar(i));
        }
        Collections.shuffle(characters, random);
        return characters;
    }
}
