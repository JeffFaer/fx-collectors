package name.falgout.jeffrey.fx.collections;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObservableKeySetTest {
  @Mock InvalidationListener invalidationListener;
  @Captor ArgumentCaptor<Observable> invalidationCaptor;

  @Mock SetChangeListener<Object> listener;
  @Captor ArgumentCaptor<Change<?>> captor;

  Object o1;
  Object o2;
  Object o3;
  ObservableMap<Object, Object> map;
  ObservableSet<Object> set;

  @Before
  public void before() {
    o1 = new Object();
    o2 = new Object();
    o3 = new Object();
    map = FXCollections.observableHashMap();
    map.put(o1, 1);
    map.put(o2, 2);

    set = new ObservableKeySet<>(map);
  }

  @Test
  public void invalidationListenerTest() {
    set.addListener(invalidationListener);

    map.put(o3, 3);
    map.remove(o1);

    set.removeListener(invalidationListener);

    map.remove(o2);

    verify(invalidationListener, times(2)).invalidated(invalidationCaptor.capture());
    assertSame(set, invalidationCaptor.getAllValues().get(0));
    assertSame(set, invalidationCaptor.getAllValues().get(1));
  }

  @Test
  public void setChangeListener() {
    set.addListener(listener);

    map.put(o3, 3);
    map.remove(o2);
    map.put(o1, 4);

    set.removeListener(listener);

    map.remove(o1);

    verify(listener, times(2)).onChanged(captor.capture());
    assertTrue(captor.getAllValues().get(0).wasAdded());
    assertSame(o3, captor.getAllValues().get(0).getElementAdded());

    assertTrue(captor.getAllValues().get(1).wasRemoved());
    assertSame(o2, captor.getAllValues().get(1).getElementRemoved());
  }
}
