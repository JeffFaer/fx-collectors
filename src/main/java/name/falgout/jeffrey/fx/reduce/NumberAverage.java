package name.falgout.jeffrey.fx.reduce;

import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableValue;

public abstract class NumberAverage<S extends Number, A extends Number> {
  private final Property<S> sum;
  private final LongProperty count;
  private final ObservableValue<A> average;

  public NumberAverage() {
    sum = new SimpleObjectProperty<>(zero());
    count = new SimpleLongProperty();

    average = Bindings.createObjectBinding(
        () -> count.get() == 0 ? null : divide(sum.getValue(), count.get()), sum, count);
  }

  protected abstract S zero();

  protected abstract S negate(S number);

  protected abstract S add(S op1, S op2);

  protected abstract A divide(S numerator, long denominator);

  public void add(S number) {
    sum.setValue(add(sum.getValue(), number));
    count.set(count.get() + 1);
  }

  public void remove(S number) {
    sum.setValue(add(sum.getValue(), negate(number)));
    count.set(count.get() - 1);
  }

  public void update(S oldNumber, S newNumber) {
    S delta = add(newNumber, negate(oldNumber));
    sum.setValue(add(sum.getValue(), delta));
  }

  public ObservableValue<S> sumProperty() {
    return sum;
  }

  public ObservableLongValue countProperty() {
    return count;
  }

  public ObservableValue<A> averageProperty() {
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
