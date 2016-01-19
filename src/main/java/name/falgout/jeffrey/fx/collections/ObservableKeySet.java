package name.falgout.jeffrey.fx.collections;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

import com.google.common.collect.ForwardingSet;

public class ObservableKeySet<E> extends ForwardingSet<E> implements ObservableSet<E> {
  private class KeySetChange extends SetChangeListener.Change<E> {
    private final MapChangeListener.Change<? extends E, ?> delegate;

    KeySetChange(MapChangeListener.Change<? extends E, ?> delegate) {
      super(ObservableKeySet.this);
      this.delegate = delegate;
    }

    @Override
    public boolean wasAdded() {
      return delegate.wasAdded();
    }

    @Override
    public boolean wasRemoved() {
      return delegate.wasRemoved();
    }

    @Override
    public E getElementAdded() {
      return delegate.getKey();
    }

    @Override
    public E getElementRemoved() {
      return delegate.getKey();
    }
  }

  private final ObservableMap<E, ?> map;

  private final Map<InvalidationListener, InvalidationListener> mappedInvalidationListeners =
      new IdentityHashMap<>();
  private final Map<SetChangeListener<? super E>, MapChangeListener<E, Object>> mappedChangeListeners =
      new IdentityHashMap<>();

  public ObservableKeySet(ObservableMap<E, ?> map) {
    this.map = map;
  }

  @Override
  protected Set<E> delegate() {
    return map.keySet();
  }

  @Override
  public void addListener(InvalidationListener listener) {
    map.addListener(
        mappedInvalidationListeners.computeIfAbsent(listener, l -> o -> l.invalidated(this)));
  }

  @Override
  public void removeListener(InvalidationListener listener) {
    map.removeListener(mappedInvalidationListeners.remove(listener));
  }

  @Override
  public void addListener(SetChangeListener<? super E> listener) {
    map.addListener(mappedChangeListeners.computeIfAbsent(listener, l -> mapChange -> {
      if (mapChange.wasAdded() && mapChange.wasRemoved()) {
        // Map::put update.
      } else {
        l.onChanged(new KeySetChange(mapChange));
      }
    }));
  }

  @Override
  public void removeListener(SetChangeListener<? super E> listener) {
    map.removeListener(mappedChangeListeners.remove(listener));
  }
}
