package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.collections.ObservableMap;

class MapCombiner<K, V, A, R> extends
    SimpleObservingMapCollector<ObservableMap<K, V>, ObservableMap<K, A>, ObservableMap<K, R>, K, V> {
  private final MapReductionHelper<K, V, A, R> helper;

  MapCombiner(ObservableMapFactory factory, UnaryOperator<ObservableMap<K, R>> finisher,
      FXCollector<? super V, A, R> downstream) {
    helper = new MapReductionHelper<>(factory, finisher, downstream);
  }

  @Override
  public Supplier<ObservableMap<K, A>> supplier() {
    return helper::getAggregateMap;
  }

  @Override
  public Function<ObservableMap<K, A>, ObservableMap<K, R>> finisher() {
    return helper::finish;
  }

  @Override
  protected void add(ObservableMap<K, A> aggregate, ObservableMap<K, V> item, K key, V value) {
    helper.add(aggregate, key, value);
  }

  @Override
  protected void remove(ObservableMap<K, A> aggregate, ObservableMap<K, V> item, K key, V value) {
    helper.remove(aggregate, key, value);
  }

  @Override
  protected void update(ObservableMap<K, A> aggregate, ObservableMap<K, V> item, K key, V oldValue,
      V newValue) {
    helper.update(aggregate, key, oldValue, newValue);
  }
}
