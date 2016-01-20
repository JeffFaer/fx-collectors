package name.falgout.jeffrey.fx.reduce;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public interface ObservableMapFactory extends MapFactory {
  @Override
  public <K, V> ObservableMap<K, V> createMap();

  public static ObservableMapFactory fromMapFactory(MapFactory factory) {
    return new ObservableMapFactory() {
      @Override
      public <K, V> ObservableMap<K, V> createMap() {
        return FXCollections.observableMap(factory.createMap());
      }
    };
  }
}
