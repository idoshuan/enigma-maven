package enigma.loader.core;

import enigma.core.Inventory;

import java.io.InputStream;

public interface Loader {
    Inventory load(String path);
    Inventory load(InputStream inputStream);
}
