package transformation.domain;

import transformation.domain.entity.Item;

import java.util.List;

public class ItemList {

    private List<Item> items;

    public ItemList(List<Item> items) {
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
