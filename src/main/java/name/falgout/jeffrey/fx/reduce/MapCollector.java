package name.falgout.jeffrey.fx.reduce;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.ObservableMap;

class MapCollector<K, V, A, R>
    extends SimpleObservingMapCollector<ObservableMap<K, V>, A, R, K, V> {
  private final FXCollector<? super Entry<K, V>, A, R> downstream;

  MapCollector(FXCollector<? super Entry<K, V>, A, R> downstream) {
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
  protected void add(A aggregate, ObservableMap<K, V> item, K key, V value) {
    downstream.add().accept(aggregate, new SimpleImmutableEntry<>(key, value));
  }

  @Override
  protected void remove(A aggregate, ObservableMap<K, V> item, K key, V value) {
    downstream.remove().accept(aggregate, new SimpleImmutableEntry<K, V>(key, value));
  }

  @Override
  protected void update(A aggregate, ObservableMap<K, V> item, K key, V oldValue, V newValue) {
    downstream.update().accept(aggregate, new SimpleImmutableEntry<K, V>(key, oldValue),
        new SimpleImmutableEntry<K, V>(key, newValue));
  }
}
