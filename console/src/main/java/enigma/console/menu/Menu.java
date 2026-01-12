package enigma.console.menu;

import java.util.List;

public interface Menu {
    String getTitle();
    void addItem(MenuItem item);
    List<MenuItem> getAllItems();
}
