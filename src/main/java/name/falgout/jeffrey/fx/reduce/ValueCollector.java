package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.beans.value.ObservableValue;

class ValueCollector<T, A, R> extends SimpleObservingValueCollector<ObservableValue<T>, A, R, T> {
  private final FXCollector<? super T, A, R> downstream;

  ValueCollector(FXCollector<? super T, A, R> downstream) {
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
  protected void add(A aggregate, ObservableValue<T> item, T value) {
    downstream.add().accept(aggregate, value);
  }

  @Override
  protected void remove(A aggregate, ObservableValue<T> item, T value) {
    downstream.remove().accept(aggregate, value);
  }

  @Override
  protected void update(A aggregate, ObservableValue<T> item, T oldValue, T newValue) {
    downstream.update().accept(aggregate, oldValue, newValue);
  }
}
