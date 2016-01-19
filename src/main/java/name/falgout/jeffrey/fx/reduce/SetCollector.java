package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.ObservableSet;

class SetCollector<E, A, R> extends SimpleObservingSetCollector<ObservableSet<E>, A, R, E> {
  private final FXCollector<? super E, A, R> downstream;

  SetCollector(FXCollector<? super E, A, R> downstream) {
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
  protected void add(A aggregate, ObservableSet<E> item, E element) {
    downstream.add().accept(aggregate, element);
  }

  @Override
  protected void remove(A aggregate, ObservableSet<E> item, E element) {
    downstream.remove().accept(aggregate, element);
  }
}
