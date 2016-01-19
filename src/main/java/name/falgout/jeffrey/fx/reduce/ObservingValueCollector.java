package name.falgout.jeffrey.fx.reduce;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class ObservingValueCollector<T, A, R, O extends ObservableValue<V>, V>
    extends ObservingCollector<T, A, R, O> {
  protected ObservingValueCollector() {}

  @Override
  protected Runnable observe(A aggregate, T item, O observable) {
    ChangeListener<V> listener = (obs, oldValue, newValue) -> {
      update(aggregate, item, observable, oldValue, newValue);
    };
    observable.addListener(listener);
    return () -> observable.removeListener(listener);
  }

  @Override
  protected final void add(A aggregate, T item, O observable) {
    add(aggregate, item, observable, observable.getValue());
  }

  @Override
  protected void remove(A aggregate, T item, O observable) {
    remove(aggregate, item, observable, observable.getValue());
  }

  protected abstract void add(A aggregate, T item, O observable, V value);

  protected abstract void remove(A aggregate, T item, O observable, V value);

  protected void update(A aggregate, T item, O observable, V oldValue, V newValue) {
    remove(aggregate, item, observable, oldValue);
    add(aggregate, item, observable, newValue);
  }
}
