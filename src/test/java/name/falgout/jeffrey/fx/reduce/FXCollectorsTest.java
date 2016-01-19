package name.falgout.jeffrey.fx.reduce;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FXCollectorsTest {
  @Mock(answer = Answers.RETURNS_DEEP_STUBS) FXCollector<Object, Object, Object> downstream;
  Object aggregate;

  @Before
  public void before() {
    aggregate = new Object();
    when(downstream.supplier().get()).thenReturn(aggregate);
  }

  @Test
  public void reduceListTest() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    ObservableList<Object> objects = FXCollections.observableArrayList(o1, o2, o3);

    FXCollectors.reduceList(objects, downstream);
    InOrder order = inOrder(downstream.add());
    order.verify(downstream.add()).accept(aggregate, o1);
    order.verify(downstream.add()).accept(aggregate, o2);
    order.verify(downstream.add()).accept(aggregate, o3);

    Object o4 = new Object();
    objects.add(o4);
    verify(downstream.add()).accept(aggregate, o4);

    objects.remove(o2);
    verify(downstream.remove()).accept(aggregate, o2);

    Object o5 = new Object();
    Object replaced = objects.set(0, o5);
    verify(downstream.remove()).accept(aggregate, replaced);
    verify(downstream.add()).accept(aggregate, o5);
  }

  @Test
  public void reduceSetTest() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    ObservableSet<Object> objects = FXCollections.observableSet(o1, o2, o3);

    FXCollectors.reduceSet(objects, downstream);
    verify(downstream.add()).accept(aggregate, o1);
    verify(downstream.add()).accept(aggregate, o2);
    verify(downstream.add()).accept(aggregate, o3);

    Object o4 = new Object();
    objects.add(o4);
    verify(downstream.add()).accept(aggregate, o4);

    objects.remove(o2);
    verify(downstream.remove()).accept(aggregate, o2);
  }
}
