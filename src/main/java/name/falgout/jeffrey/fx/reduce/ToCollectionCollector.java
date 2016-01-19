package name.falgout.jeffrey.fx.reduce;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

class ToCollectionCollector<T, C extends Collection<T>> implements FXCollector<T, C, C> {
  private final Supplier<C> ctor;
  private final UnaryOperator<C> finisher;

  ToCollectionCollector(Supplier<C> ctor, UnaryOperator<C> finisher) {
    this.ctor = ctor;
    this.finisher = finisher;
  }

  @Override
  public Supplier<C> supplier() {
    return ctor;
  }

  @Override
  public BiConsumer<C, T> add() {
    return C::add;
  }

  @Override
  public BiConsumer<C, T> remove() {
    return C::remove;
  }

  @Override
  public Function<C, C> finisher() {
    return finisher;
  }
}
