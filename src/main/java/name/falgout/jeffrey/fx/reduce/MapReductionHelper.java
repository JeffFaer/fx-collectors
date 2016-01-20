package name.falgout.jeffrey.fx.reduce;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import javafx.collections.ObservableMap;

import name.falgout.jeffrey.fx.collections.MappedValues;

class MapReductionHelper<K, V, A, R> {
  private final ObservableMapFactory factory;
  private final UnaryOperator<ObservableMap<K, R>> finisher;
  private final FXCollector<? super V, A, R> downstream;

  private final Map<A, Integer> counts = new IdentityHashMap<>();

  MapReductionHelper(ObservableMapFactory factory, UnaryOperator<ObservableMap<K, R>> finisher,
      FXCollector<? super V, A, R> downstream) {
    this.factory = factory;
    this.finisher = finisher;
    this.downstream = downstream;
  }

  public ObservableMap<K, A> getAggregateMap() {
    return factory.createMap();
  }

  public ObservableMap<K, R> finish(ObservableMap<K, A> aggregate) {
    ObservableMap<K, R> mapped = new MappedValues<>(aggregate, downstream.finisher());
    return finisher.apply(mapped);
  }

  private int add(int i1, int i2) {
    return i1 + i2;
  }

  public void add(ObservableMap<K, A> aggregate, K key, V value) {
    A downstreamAggregate = aggregate.computeIfAbsent(key, k -> downstream.supplier().get());
    downstream.add().accept(downstreamAggregate, value);

    counts.merge(downstreamAggregate, 1, this::add);
  }

  public void remove(ObservableMap<K, A> aggregate, K key, V value) {
    A downstreamAggregate = aggregate.get(key);
    downstream.remove().accept(downstreamAggregate, value);

    int count = counts.merge(downstreamAggregate, -1, this::add);
    if (count == 0) {
      aggregate.remove(key);
      counts.remove(downstreamAggregate);
    }
  }

  public void update(ObservableMap<K, A> aggregate, K key, V oldValue, V newValue) {
    A downstreamAggregate = aggregate.get(key);
    downstream.update().accept(downstreamAggregate, oldValue, newValue);
  }
}
