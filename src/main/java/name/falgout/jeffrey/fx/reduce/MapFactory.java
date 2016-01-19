package name.falgout.jeffrey.fx.reduce;

import java.util.Map;

public interface MapFactory {
  public <K, V> Map<K, V> createMap();
}
