package name.falgout.jeffrey.fx.reduce;

import java.util.Optional;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;

public abstract class NumberAverage<S extends Number, A extends Number> {
  private final Binding<S> sumBinding;
  private final LongBinding countBinding;
  private final Binding<A> average;

  private S sum;
  private long count = 0;

  public NumberAverage() {
    sum = zero();

    sumBinding = Bindings.createObjectBinding(() -> sum);
    countBinding = Bindings.createLongBinding(() -> count);
    average = Bindings.createObjectBinding(() -> count == 0 ? null : divide(sum, count), sumBinding,
        countBinding);
  }

  protected abstract S zero();

  protected abstract S negate(S number);

  protected abstract S add(S op1, S op2);

  protected abstract A divide(S numerator, long denominator);

  public void add(S number) {
    sum = add(sum, number);
    count += 1;

    sumBinding.invalidate();
    countBinding.invalidate();
  }

  public void remove(S number) {
    sum = add(sum, negate(number));
    count -= 1;

    sumBinding.invalidate();
    countBinding.invalidate();
  }

  public void update(S oldNumber, S newNumber) {
    sum = add(sum, newNumber);
    sum = add(sum, negate(oldNumber));

    sumBinding.invalidate();
  }

  public Binding<S> sumProperty() {
    return sumBinding;
  }

  public LongBinding countProperty() {
    return countBinding;
  }

  public Binding<A> averageProperty() {
    return average;
  }

  public S getSum() {
    return sumProperty().getValue();
  }

  public long getCount() {
    return countProperty().get();
  }

  public Optional<A> getAverage() {
    return Optional.ofNullable(averageProperty().getValue());
  }
}
