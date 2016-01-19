package name.falgout.jeffrey.fx.reduce;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import name.falgout.jeffrey.fx.collections.ObservableKeySet;

class ToSet<T> implements FXCollector<T, ObservableMap<T, Object>, ObservableSet<T>> {
  private static final FXCollector<Object, Object, Object> NULL_COLLECTOR =
      new FXCollector<Object, Object, Object>() {
        @Override
        public Supplier<Object> supplier() {
          return Object::new;
        }

        @Override
        public BiConsumer<Object, Object> add() {
          return (a, t) -> {
          };
        }

        @Override
        public BiConsumer<Object, Object> remove() {
          return (a, t) -> {
          };
        }

        @Override
        public Function<Object, Object> finisher() {
          return Function.identity();
        }
      };
  private final MapReductionHelper<T, Object, Object, Object> helper;
  private final UnaryOperator<ObservableSet<T>> finisher;

  ToSet(MapFactory factory, UnaryOperator<ObservableSet<T>> finisher) {
    helper = new MapReductionHelper<>(factory, UnaryOperator.identity(), NULL_COLLECTOR);
    this.finisher = finisher;
  }

  @Override
  public Supplier<ObservableMap<T, Object>> supplier() {
    return helper::getAggregateMap;
  }

  @Override
  public BiConsumer<ObservableMap<T, Object>, T> add() {
    return (a, t) -> helper.add(a, t, null);
  }

  @Override
  public BiConsumer<ObservableMap<T, Object>, T> remove() {
    return (a, t) -> helper.remove(a, t, null);
  }

  @Override
  public Function<ObservableMap<T, Object>, ObservableSet<T>> finisher() {
    return m -> finisher.apply(new ObservableKeySet<>(m));
  }
}
