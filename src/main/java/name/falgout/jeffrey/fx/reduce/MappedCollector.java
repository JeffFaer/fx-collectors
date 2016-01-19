package name.falgout.jeffrey.fx.reduce;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

class MappedCollector<T, U, A, R> implements FXCollector<T, A, R> {
  private final Function<? super T, ? extends U> map;
  private final FXCollector<? super U, A, R> downstream;

  MappedCollector(Function<? super T, ? extends U> map, FXCollector<? super U, A, R> downstream) {
    this.map = map;
    this.downstream = downstream;
  }

  @Override
  public Supplier<A> supplier() {
    return downstream.supplier();
  }

  @Override
  public BiConsumer<A, T> add() {
    return (a, t) -> {
      downstream.add().accept(a, map.apply(t));
    };
  }

  @Override
  public BiConsumer<A, T> remove() {
    return (a, t) -> {
      downstream.remove().accept(a, map.apply(t));
    };
  }

  @Override
  public Function<A, R> finisher() {
    return downstream.finisher();
  }
}
