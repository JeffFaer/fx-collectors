package name.falgout.jeffrey.fx.reduce;

import java.util.function.Function;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class FXCollectors {
  private FXCollectors() {}

  public static <T, A, R> R reduce(T item, FXCollector<? super T, A, R> reduction) {
    A aggregate = reduction.supplier().get();
    reduction.add().accept(aggregate, item);
    return reduction.finisher().apply(aggregate);
  }

  public static <T, R> R reduceList(ObservableList<? extends T> list,
      FXCollector<? super T, ?, R> reduction) {
    return reduce(list, reducingLists(reduction));
  }

  public static <T, R> R reduceSet(ObservableSet<? extends T> set,
      FXCollector<? super T, ?, R> reduction) {
    return reduce(set, reducingSets(reduction));
  }

  public static <T, R> FXCollector<ObservableList<T>, ?, R>
      reducingLists(FXCollector<? super T, ?, R> downstream) {
    return new ListCollector<>(downstream);
  }

  public static <T, R> FXCollector<ObservableSet<T>, ?, R>
      reducingSets(FXCollector<? super T, ?, R> downstream) {
    return new SetCollector<>(downstream);
  }

  public static <T, R> FXCollector<ObservableValue<T>, ?, R>
      observing(FXCollector<? super T, ?, R> downstream) {
    return new ValueCollector<>(downstream);
  }

  public static <T, U, R> FXCollector<T, ?, R> mapping(Function<? super T, ? extends U> map,
      FXCollector<? super U, ?, R> downstream) {
    return new MappedCollector<>(map, downstream);
  }

  public static <T, U, R> FXCollector<T, ?, R> observableMapping(
      Function<? super T, ? extends ObservableValue<U>> map,
      FXCollector<? super U, ?, R> downstream) {
    return mapping(map, observing(downstream));
  }
}
