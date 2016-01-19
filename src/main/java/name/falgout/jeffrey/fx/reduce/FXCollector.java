package name.falgout.jeffrey.fx.reduce;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FXCollector<T, A, R> {
  public interface UpdateFunction<A, T> {
    public void accept(A aggregate, T oldValue, T newValue);
  }

  public Supplier<A> supplier();

  public BiConsumer<A, T> add();

  public BiConsumer<A, T> remove();

  default UpdateFunction<A, T> update() {
    return (a, oldValue, newValue) -> {
      remove().accept(a, oldValue);
      add().accept(a, newValue);
    };
  }

  public Function<A, R> finisher();
}
