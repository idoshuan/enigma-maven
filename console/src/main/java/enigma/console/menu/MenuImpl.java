package enigma.console.menu;

import java.util.ArrayList;
import java.util.List;

public class MenuImpl implements Menu {

    private final String title;
    private final List<MenuItem> items;

    public MenuImpl(String title) {
        this.title = title;
        this.items = new ArrayList<>();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void addItem(MenuItem item) {
        items.add(item);
    }

    @Override
    public List<MenuItem> getAllItems() {
        return new ArrayList<>(items);
    }
}
