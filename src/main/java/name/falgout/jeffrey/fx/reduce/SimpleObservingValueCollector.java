package name.falgout.jeffrey.fx.reduce;

import javafx.beans.value.ObservableValue;

public abstract class SimpleObservingValueCollector<T extends ObservableValue<V>, A, R, V>
    extends ObservingValueCollector<T, A, R, T, V> {
  protected SimpleObservingValueCollector() {}

  @Override
  protected final T getObservable(A aggregate, T item) {
    return item;
  }

  @Override
  protected final void add(A aggregate, T item, T observable, V value) {
    add(aggregate, item, value);
  }

  @Override
  protected void remove(A aggregate, T item, T observable, V value) {
    remove(aggregate, item, value);
  }

  @Override
  protected void update(A aggregate, T item, T observable, V oldValue, V newValue) {
    update(aggregate, item, oldValue, newValue);
  }

  protected abstract void add(A aggregate, T item, V value);

  protected abstract void remove(A aggregate, T item, V value);

  protected void update(A aggregate, T item, V oldValue, V newValue) {
    super.update(aggregate, item, item, oldValue, newValue);
  }
}
