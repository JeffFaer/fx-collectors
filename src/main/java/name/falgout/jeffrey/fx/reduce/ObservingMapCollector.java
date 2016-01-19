package name.falgout.jeffrey.fx.reduce;

import java.util.Map.Entry;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

public abstract class ObservingMapCollector<T, A, R, M extends ObservableMap<K, V>, K, V>
    extends ObservingCollector<T, A, R, M> {
  protected ObservingMapCollector() {}

  @Override
  protected final Runnable observe(A aggregate, T item, M observable) {
    MapChangeListener<K, V> listener = mapChange -> {
      if (mapChange.wasAdded() && mapChange.wasRemoved()) {
        update(aggregate, item, observable, mapChange.getKey(), mapChange.getValueRemoved(),
            mapChange.getValueAdded());
      } else if (mapChange.wasAdded()) {
        add(aggregate, item, observable, mapChange.getKey(), mapChange.getValueAdded());
      } else if (mapChange.wasRemoved()) {
        remove(aggregate, item, observable, mapChange.getKey(), mapChange.getValueRemoved());
      }
    };
    observable.addListener(listener);
    return () -> observable.removeListener(listener);
  }

  @Override
  protected void add(A aggregate, T item, M observable) {
    for (Entry<K, V> e : observable.entrySet()) {
      add(aggregate, item, observable, e.getKey(), e.getValue());
    }
  }

  @Override
  protected void remove(A aggregate, T item, M observable) {
    for (Entry<K, V> e : observable.entrySet()) {
      remove(aggregate, item, observable, e.getKey(), e.getValue());
    }
  }

  protected abstract void add(A aggregate, T item, M observable, K key, V value);

  protected abstract void remove(A aggregate, T item, M observable, K key, V value);

  protected void update(A aggregate, T item, M observable, K key, V oldValue, V newValue) {
    remove(aggregate, item, observable, key, oldValue);
    add(aggregate, item, observable, key, newValue);
  }
}
