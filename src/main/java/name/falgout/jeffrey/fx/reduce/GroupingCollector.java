package name.falgout.jeffrey.fx.reduce;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import name.falgout.jeffrey.fx.collections.MappedValues;

class GroupingCollector<T, A, R, G> extends
    ObservingValueCollector<T, ObservableMap<G, A>, ObservableMap<G, R>, ObservableValue<G>, G> {
  private final Function<? super T, ? extends ObservableValue<G>> grouper;
  private final MapFactory factory;
  private final UnaryOperator<ObservableMap<G, R>> finisher;
  private final FXCollector<? super T, A, R> downstream;

  private final Map<A, Integer> counts = new IdentityHashMap<>();

  GroupingCollector(Function<? super T, ? extends ObservableValue<G>> grouper, MapFactory factory,
      UnaryOperator<ObservableMap<G, R>> finisher, FXCollector<? super T, A, R> downstream) {
    this.grouper = grouper;
    this.factory = factory;
    this.finisher = finisher;
    this.downstream = downstream;
  }

  @Override
  protected ObservableValue<G> getObservable(ObservableMap<G, A> aggregate, T item) {
    return grouper.apply(item);
  }

  @Override
  public Supplier<ObservableMap<G, A>> supplier() {
    return () -> FXCollections.observableMap(factory.createMap());
  }

  @Override
  public Function<ObservableMap<G, A>, ObservableMap<G, R>> finisher() {
    return a -> {
      ObservableMap<G,R> mapped = new MappedValues<>(a, downstream.finisher());
      return finisher.apply(mapped);
    };
  }

  @Override
  protected void add(ObservableMap<G, A> aggregate, T item, ObservableValue<G> observable,
      G value) {
    A downstreamAggregate = aggregate.computeIfAbsent(value, g -> downstream.supplier().get());
    downstream.add().accept(downstreamAggregate, item);

    counts.merge(downstreamAggregate, 1, GroupingCollector::add);
  }

  @Override
  protected void remove(ObservableMap<G, A> aggregate, T item, ObservableValue<G> observable,
      G value) {
    A downstreamAggregate = aggregate.get(value);
    downstream.remove().accept(downstreamAggregate, item);

    int count = counts.merge(downstreamAggregate, -1, GroupingCollector::add);
    if (count == 0) {
      aggregate.remove(value);
      counts.remove(downstreamAggregate);
    }
  }

  private static Integer add(Integer i1, Integer i2) {
    return i1 + i2;
  }
}
