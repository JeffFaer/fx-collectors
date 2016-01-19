package name.falgout.jeffrey.fx.collections;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.collections.MapChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;

public class MappedValues<K, V, R> extends ForwardingMap<K, R> implements ObservableMap<K, R> {
  private class MapChange extends Change<K, R> {
    private final Change<? extends K, ? extends V> delegate;

    MapChange(Change<? extends K, ? extends V> delegate) {
      super(MappedValues.this);
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
    public K getKey() {
      return delegate.getKey();
    }

    @Override
    public R getValueAdded() {
      return map.apply(delegate.getValueAdded());
    }

    @Override
    public R getValueRemoved() {
      return map.apply(delegate.getValueRemoved());
    }
  }

  private final ObservableMap<K, V> source;
  private final Map<K, R> delegate;
  private final Function<? super V, ? extends R> map;

  private final Map<InvalidationListener, InvalidationListener> mappedInvalidationListeners =
      new IdentityHashMap<>();
  private final Map<MapChangeListener<? super K, ? super R>, MapChangeListener<K, V>> mappedChangeListeners =
      new IdentityHashMap<>();

  public MappedValues(ObservableMap<K, V> source, Function<? super V, ? extends R> map) {
    this.source = source;
    delegate = Maps.transformValues(source, map::apply);
    this.map = map;
  }

  @Override
  protected Map<K, R> delegate() {
    return delegate;
  }

  @Override
  public void addListener(InvalidationListener listener) {
    source.addListener(
        mappedInvalidationListeners.computeIfAbsent(listener, l -> o -> l.invalidated(this)));
  }

  @Override
  public void removeListener(InvalidationListener listener) {
    source.removeListener(mappedInvalidationListeners.get(listener));
  }

  @Override
  public void addListener(MapChangeListener<? super K, ? super R> listener) {
    source.addListener(
        mappedChangeListeners.computeIfAbsent(listener, l -> c -> l.onChanged(new MapChange(c))));
  }

  @Override
  public void removeListener(MapChangeListener<? super K, ? super R> listener) {
    source.removeListener(mappedChangeListeners.get(listener));
  }
}
