package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

class GroupingCollector<T, A, R, G> extends
    ObservingValueCollector<T, ObservableMap<G, A>, ObservableMap<G, R>, ObservableValue<G>, G> {
  private final Function<? super T, ? extends ObservableValue<G>> grouper;
  private final MapReductionHelper<G, T, A, R> helper;

  GroupingCollector(Function<? super T, ? extends ObservableValue<G>> grouper,
      ObservableMapFactory factory, UnaryOperator<ObservableMap<G, R>> finisher,
      FXCollector<? super T, A, R> downstream) {
    this.grouper = grouper;
    helper = new MapReductionHelper<>(factory, finisher, downstream);
  }

  @Override
  protected ObservableValue<G> getObservable(ObservableMap<G, A> aggregate, T item) {
    return grouper.apply(item);
  }

  @Override
  public Supplier<ObservableMap<G, A>> supplier() {
    return helper::getAggregateMap;
  }

  @Override
  public Function<ObservableMap<G, A>, ObservableMap<G, R>> finisher() {
    return helper::finish;
  }

  @Override
  protected void add(ObservableMap<G, A> aggregate, T item, ObservableValue<G> observable,
      G value) {
    helper.add(aggregate, value, item);
  }

  @Override
  protected void remove(ObservableMap<G, A> aggregate, T item, ObservableValue<G> observable,
      G value) {
    helper.remove(aggregate, value, item);
  }
}
