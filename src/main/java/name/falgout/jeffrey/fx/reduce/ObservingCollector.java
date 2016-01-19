package name.falgout.jeffrey.fx.reduce;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javafx.beans.Observable;

abstract class ObservingCollector<T, A, R, O extends Observable>
    implements FXCollector<T, A, R> {
  private final Map<A, Map<T, Runnable>> subscriptions = new IdentityHashMap<>();

  protected ObservingCollector() {}

  protected abstract O getObservable(A aggregate, T item);

  protected abstract Runnable observe(A aggregate, T item, O observable);

  private void addSubscription(A aggregate, T item, Runnable subscription) {
    subscriptions.computeIfAbsent(aggregate, k -> new IdentityHashMap<>()).put(item, subscription);
  }

  private void removeSubscription(A aggregate, T item) {
    subscriptions.get(aggregate).get(item).run();
  }

  @Override
  public final BiConsumer<A, T> add() {
    return (a, t) -> {
      O o = getObservable(a, t);
      addSubscription(a, t, observe(a, t, o));
      add(a, t, o);
    };
  }

  @Override
  public final BiConsumer<A, T> remove() {
    return (a, t) -> {
      O o = getObservable(a, t);
      removeSubscription(a, t);
      remove(a, t, o);
    };
  }

  protected abstract void add(A aggregate, T item, O observable);

  protected abstract void remove(A aggregate, T item, O observable);
}
