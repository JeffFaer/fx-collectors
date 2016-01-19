package name.falgout.jeffrey.fx.reduce;

import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public abstract class ObservingSetCollector<T, A, R, S extends ObservableSet<E>, E>
    extends ObservingCollector<T, A, R, S> {
  protected ObservingSetCollector() {}

  @Override
  protected final Runnable observe(A aggregate, T item, S observable) {
    SetChangeListener<E> listener = setChange -> {
      if (setChange.wasAdded()) {
        add(aggregate, item, observable, setChange.getElementAdded());
      } else if (setChange.wasRemoved()) {
        remove(aggregate, item, observable, setChange.getElementRemoved());
      }
    };
    observable.addListener(listener);
    return () -> observable.removeListener(listener);
  }

  @Override
  protected void add(A aggregate, T item, S observable) {
    for (E e : observable) {
      add(aggregate, item, observable, e);
    }
  }

  @Override
  protected void remove(A aggregate, T item, S observable) {
    for (E e : observable) {
      remove(aggregate, item, observable, e);
    }
  }

  protected abstract void add(A aggregate, T item, S observable, E element);

  protected abstract void remove(A aggregate, T item, S observable, E element);
}
