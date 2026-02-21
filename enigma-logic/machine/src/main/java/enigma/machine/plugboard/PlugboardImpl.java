package enigma.machine.plugboard;

import java.io.Serializable;

public class PlugboardImpl implements Plugboard, Serializable {

    private final int[] mapping;
    private final boolean hasPairs;

    private PlugboardImpl(int[] mapping, boolean hasPairs) {
        this.mapping = mapping;
        this.hasPairs = hasPairs;
    }

    public static Plugboard identity(int alphabetSize) {
        int[] mapping = new int[alphabetSize];
        for (int i = 0; i < alphabetSize; i++) {
            mapping[i] = i;
        }
        return new PlugboardImpl(mapping, false);
    }

    public static Plugboard fromIndexPairs(int alphabetSize, int[][] pairs) {
        int[] mapping = new int[alphabetSize];
        for (int i = 0; i < alphabetSize; i++) {
            mapping[i] = i;
        }

        for (int[] pair : pairs) {
            int index1 = pair[0];
            int index2 = pair[1];
            mapping[index1] = index2;
            mapping[index2] = index1;
        }

        return new PlugboardImpl(mapping, pairs.length > 0);
    }

    @Override
    public int swap(int index) {
        if (index < 0 || index >= mapping.length) {
            return index;
        }
        return mapping[index];
    }
}

