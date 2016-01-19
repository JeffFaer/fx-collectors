package name.falgout.jeffrey.fx.reduce;

import java.util.List;
import java.util.ListIterator;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

abstract class ObservingListCollector<T, A, R, L extends ObservableList<E>, E>
    extends ObservingCollector<T, A, R, L> {
  protected ObservingListCollector() {}

  @Override
  protected final Runnable observe(A aggregate, T item, L observable) {
    ListChangeListener<E> listener = c -> {
      while (c.next()) {
        if (c.wasReplaced()) {
          ListIterator<? extends E> oldValues = c.getRemoved().listIterator(c.getRemovedSize());
          ListIterator<? extends E> newValues = c.getAddedSubList().listIterator(c.getAddedSize());
          while (oldValues.hasPrevious()) {
            int i = oldValues.previousIndex() + c.getFrom();
            update(aggregate, item, observable, oldValues.previous(), newValues.previous(), i);
          }
        } else if (c.wasRemoved()) {
          remove(aggregate, item, observable, c.getRemoved(), c.getFrom());
        } else if (c.wasAdded()) {
          add(aggregate, item, observable, c.getAddedSubList(), c.getFrom());
        }
      }
    };
    observable.addListener(listener);
    return () -> observable.removeListener(listener);
  }

  @Override
  protected final void add(A aggregate, T item, L observable) {
    add(aggregate, item, observable, observable, 0);
  }

  protected void add(A aggregate, T item, L observable, List<? extends E> items, int offset) {
    ListIterator<? extends E> itr = items.listIterator();
    while (itr.hasNext()) {
      int i = itr.nextIndex() + offset;
      add(aggregate, item, observable, itr.next(), i);
    }
  }

  @Override
  protected final void remove(A aggregate, T item, L observable) {
    remove(aggregate, item, observable, observable, 0);
  }

  protected void remove(A aggregate, T item, L observable, List<? extends E> items, int offset) {
    ListIterator<? extends E> itr = items.listIterator(items.size());
    while (itr.hasPrevious()) {
      int i = itr.previousIndex() + offset;
      remove(aggregate, item, observable, itr.previous(), i);
    }
  }

  protected abstract void add(A aggregate, T item, L observable, E element, int index);

  protected abstract void remove(A aggregate, T item, L observable, E element, int index);

  protected void update(A aggregate, T item, L observable, E oldElement, E newElement, int index) {
    remove(aggregate, item, observable, oldElement, index);
    add(aggregate, item, observable, newElement, index);
  }
}
