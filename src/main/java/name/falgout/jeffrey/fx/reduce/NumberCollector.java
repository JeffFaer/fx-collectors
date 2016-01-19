package name.falgout.jeffrey.fx.reduce;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

class NumberCollector<S extends Number, A extends Number>
    implements FXCollector<S, NumberAverage<S, A>, NumberAverage<S, A>> {
  private final NumberAverage<S, A> average;

  NumberCollector(NumberAverage<S, A> average) {
    this.average = average;
  }

  @Override
  public Supplier<NumberAverage<S, A>> supplier() {
    return () -> average;
  }

  @Override
  public BiConsumer<NumberAverage<S, A>, S> add() {
    return NumberAverage::add;
  }

  @Override
  public BiConsumer<NumberAverage<S, A>, S> remove() {
    return NumberAverage::remove;
  }

  @Override
  public UpdateFunction<NumberAverage<S, A>, S> update() {
    return NumberAverage::update;
  }

  @Override
  public Function<NumberAverage<S, A>, NumberAverage<S, A>> finisher() {
    return Function.identity();
  }
}
