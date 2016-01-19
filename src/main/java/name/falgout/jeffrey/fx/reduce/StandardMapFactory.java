package name.falgout.jeffrey.fx.reduce;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public enum StandardMapFactory implements MapFactory {
  LINKED_HASH_MAP {
    @Override
    public <K, V> Map<K, V> createMap() {
      return new LinkedHashMap<>();
    }
  },
  TREE_MAP {
    @Override
    public <K, V> Map<K, V> createMap() {
      return new TreeMap<>();
    }
  };

  @Override
  public abstract <K, V> Map<K, V> createMap();
}
