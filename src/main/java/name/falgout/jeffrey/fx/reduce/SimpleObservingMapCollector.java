package name.falgout.jeffrey.fx.reduce;

import javafx.collections.ObservableMap;

public abstract class SimpleObservingMapCollector<T extends ObservableMap<K, V>, A, R, K, V>
    extends ObservingMapCollector<T, A, R, T, K, V> {
  protected SimpleObservingMapCollector() {}

  @Override
  protected final T getObservable(A aggregate, T item) {
    return item;
  }

  @Override
  protected final void add(A aggregate, T item, T observable) {
    add(aggregate, item);
  }

  @Override
  protected final void remove(A aggregate, T item, T observable) {
    remove(aggregate, item);
  }

  @Override
  protected final void add(A aggregate, T item, T observable, K key, V value) {
    add(aggregate, item, key, value);
  }

  @Override
  protected final void remove(A aggregate, T item, T observable, K key, V value) {
    remove(aggregate, item, key, value);
  }

  @Override
  protected final void update(A aggregate, T item, T observable, K key, V oldValue, V newValue) {
    update(aggregate, item, key, oldValue, newValue);
  }

  protected void add(A aggregate, T item) {
    super.add(aggregate, item, item);
  }

  protected void remove(A aggregate, T item) {
    super.remove(aggregate, item, item);
  }

  protected abstract void add(A aggregate, T item, K key, V value);

  protected abstract void remove(A aggregate, T item, K key, V value);

  protected void update(A aggregate, T item, K key, V oldValue, V newValue) {
    super.update(aggregate, item, item, key, oldValue, newValue);
  }
}
