package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.ObservableList;

class ListCollector<E, A, R> extends SimpleObservingListCollector<ObservableList<E>, A, R, E> {
  private final FXCollector<? super E, A, R> downstream;

  ListCollector(FXCollector<? super E, A, R> downstream) {
    this.downstream = downstream;
  }

  @Override
  public Supplier<A> supplier() {
    return downstream.supplier();
  }

  @Override
  public Function<A, R> finisher() {
    return downstream.finisher();
  }

  @Override
  protected void add(A aggregate, ObservableList<E> item, E element, int index) {
    downstream.add().accept(aggregate, element);
  }

  @Override
  protected void remove(A aggregate, ObservableList<E> item, E element, int index) {
    downstream.remove().accept(aggregate, element);
  }

  @Override
  protected void update(A aggregate, ObservableList<E> item, E oldElement, E newElement,
      int index) {
    downstream.update().accept(aggregate, oldElement, newElement);
  }
}
