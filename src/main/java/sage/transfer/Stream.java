package sage.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Stream {
  private List<Item> items;

  public Stream() {
    items = new ArrayList<>();
  }

  public Stream(List<? extends Item> _items) {
    items = new ArrayList<>(_items);
  }

  public void add(Item item) {
    getItems().add(item);
  }

  public void addAll(Collection<? extends Item> _items) {
    getItems().addAll(_items);
  }

  public List<Item> getItems() {
    return items;
  }

  @Override
  public String toString() {
    return "Stream tweets count: " + items.size();
  }
}
