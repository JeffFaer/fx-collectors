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

  default <RR> FXCollector<T, A, RR> andThen(Function<? super R, ? extends RR> after) {
    Function<A, RR> finisher = finisher().andThen(after);

    return new FXCollector<T, A, RR>() {
      @Override
      public Supplier<A> supplier() {
        return FXCollector.this.supplier();
      }

      @Override
      public BiConsumer<A, T> add() {
        return FXCollector.this.add();
      }

      @Override
      public BiConsumer<A, T> remove() {
        return FXCollector.this.remove();
      }

      @Override
      public UpdateFunction<A, T> update() {
        return FXCollector.this.update();
      }

      @Override
      public Function<A, RR> finisher() {
        return finisher;
      }
    };
  }
}
