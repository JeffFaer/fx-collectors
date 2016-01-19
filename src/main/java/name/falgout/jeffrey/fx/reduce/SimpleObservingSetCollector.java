package name.falgout.jeffrey.fx.reduce;

import javafx.collections.ObservableSet;

abstract class SimpleObservingSetCollector<T extends ObservableSet<E>, A, R, E>
    extends ObservingSetCollector<T, A, R, T, E> {
  protected SimpleObservingSetCollector() {}

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
  protected final void add(A aggregate, T item, T observable, E element) {
    add(aggregate, item, element);
  }

  @Override
  protected final void remove(A aggregate, T item, T observable, E element) {
    remove(aggregate, item, element);
  }

  protected void add(A aggregate, T item) {
    super.add(aggregate, item, item);
  }

  protected void remove(A aggregate, T item) {
    super.remove(aggregate, item, item);
  }

  protected abstract void add(A aggregate, T item, E element);

  protected abstract void remove(A aggregate, T item, E element);
}
