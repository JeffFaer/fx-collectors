package name.falgout.jeffrey.fx.reduce;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FXCollector<T, A, R> {
  public Supplier<A> supplier();

  public BiConsumer<A, T> add();

  public BiConsumer<A, T> remove();

  public Function<A, R> finisher();
}
