package name.falgout.jeffrey.fx.reduce;

import java.util.List;
import java.util.ListIterator;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public abstract class ObservingListCollector<T, A, R, L extends ObservableList<E>, E>
    extends ObservingCollector<T, A, R, L> {
  protected ObservingListCollector() {}

  @Override
  protected final Runnable observe(A aggregate, T item, L observable) {
    ListChangeListener<E> listener = listChange -> {
      while (listChange.next()) {
        if (listChange.wasReplaced()) {
          ListIterator<? extends E> oldValues =
              listChange.getRemoved().listIterator(listChange.getRemovedSize());
          ListIterator<? extends E> newValues =
              listChange.getAddedSubList().listIterator(listChange.getAddedSize());
          while (oldValues.hasPrevious()) {
            int i = oldValues.previousIndex() + listChange.getFrom();
            update(aggregate, item, observable, oldValues.previous(), newValues.previous(), i);
          }
        } else if (listChange.wasRemoved()) {
          remove(aggregate, item, observable, listChange.getRemoved(), listChange.getFrom());
        } else if (listChange.wasAdded()) {
          add(aggregate, item, observable, listChange.getAddedSubList(), listChange.getFrom());
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
