package name.falgout.jeffrey.fx.reduce;

import java.util.List;

import javafx.collections.ObservableList;

public abstract class SimpleObservingListCollector<T extends ObservableList<E>, A, R, E>
    extends ObservingListCollector<T, A, R, T, E> {
  protected SimpleObservingListCollector() {}

  @Override
  protected final T getObservable(A aggregate, T item) {
    return item;
  }

  @Override
  protected final void add(A aggregate, T item, T observable, List<? extends E> items, int offset) {
    add(aggregate, item, items, offset);
  }

  @Override
  protected final void remove(A aggregate, T item, T observable, List<? extends E> items,
      int offset) {
    remove(aggregate, item, items, offset);
  }

  @Override
  protected final void add(A aggregate, T item, T observable, E element, int index) {
    add(aggregate, item, element, index);
  }

  @Override
  protected final void remove(A aggregate, T item, T observable, E element, int index) {
    remove(aggregate, item, element, index);
  }

  @Override
  protected final void update(A aggregate, T item, T observable, E oldElement, E newElement,
      int index) {
    update(aggregate, item, oldElement, newElement, index);
  }

  protected void add(A aggregate, T item, List<? extends E> items, int offset) {
    super.add(aggregate, item, item, items, offset);
  }

  protected void remove(A aggregate, T item, List<? extends E> items, int offset) {
    super.remove(aggregate, item, item, items, offset);
  }

  protected abstract void add(A aggregate, T item, E element, int index);

  protected abstract void remove(A aggregate, T item, E element, int index);

  protected void update(A aggregate, T item, E oldElement, E newElement, int index) {
    super.update(aggregate, item, item, oldElement, newElement, index);
  }
}
