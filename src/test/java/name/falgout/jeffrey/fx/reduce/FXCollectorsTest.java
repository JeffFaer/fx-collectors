package name.falgout.jeffrey.fx.reduce;

import static name.falgout.jeffrey.fx.reduce.FXCollectors.averagingIntegers;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.combineMaps;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.observing;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.toList;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.toSet;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.function.Function;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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

  @Mock InvalidationListener listener;

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
    verify(downstream.update()).accept(aggregate, replaced, o5);
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

  @Test
  public void reduceMapTest() {
    Object o1 = new Object();
    Object o2 = new Object();
    Object o3 = new Object();
    ObservableMap<Object, Integer> objects = FXCollections.observableMap(new LinkedHashMap<>());
    objects.put(o1, 1);
    objects.put(o2, 2);
    objects.put(o3, 3);

    FXCollectors.reduceMap(objects, downstream);
    verify(downstream.add()).accept(aggregate, new SimpleImmutableEntry<>(o1, 1));
    verify(downstream.add()).accept(aggregate, new SimpleImmutableEntry<>(o2, 2));
    verify(downstream.add()).accept(aggregate, new SimpleImmutableEntry<>(o3, 3));

    Object o4 = new Object();
    objects.put(o4, 4);
    verify(downstream.add()).accept(aggregate, new SimpleImmutableEntry<>(o4, 4));

    objects.remove(o2);
    verify(downstream.remove()).accept(aggregate, new SimpleImmutableEntry<>(o2, 2));

    objects.put(o3, 5);
    verify(downstream.update()).accept(aggregate, new SimpleImmutableEntry<>(o3, 3),
        new SimpleImmutableEntry<>(o3, 5));
  }

  @Test
  public void reduceObservableValue() {
    Property<Object> prop = new SimpleObjectProperty<>();
    FXCollector<ObservableValue<Object>, ?, ?> coll = observing(downstream);

    FXCollectors.reduce(prop, coll);
    verify(downstream.add()).accept(aggregate, null);

    prop.setValue(new Object());
    verify(downstream.update()).accept(aggregate, null, prop.getValue());
  }

  @Test
  public void integerAverage() {
    ObservableList<Integer> numbers = FXCollections.observableArrayList();

    NumberAverage<Integer, Double> average = FXCollectors.reduceList(numbers, averagingIntegers());
    assertEquals(0, (int) average.getSum());
    assertEquals(0, average.getCount());
    assertFalse(average.getAverage().isPresent());

    numbers.addAll(1, 2, 3, 4, 5);
    assertEquals(5, average.getCount());
    assertEquals(15, (int) average.getSum());
    assertEquals(3.0, average.getAverage().get(), 0);

    average.sumProperty().addListener(listener);
    average.countProperty().addListener(listener);
    numbers.set(0, 5);
    assertEquals(19, (int) average.getSum());

    // Verify that update was called correctly.
    verify(listener).invalidated(average.sumProperty());
    verifyNoMoreInteractions(listener);
  }

  @Test
  public void groupBy() {
    IntegerProperty p1 = new SimpleIntegerProperty();
    IntegerProperty p2 = new SimpleIntegerProperty();
    IntegerProperty p3 = new SimpleIntegerProperty();
    IntegerProperty p4 = new SimpleIntegerProperty();
    ObservableList<IntegerProperty> numbers = FXCollections.observableArrayList(p1, p2, p3, p4);

    Function<IntegerProperty, ObservableValue<Number>> group = t -> t;
    ObservableMap<Number, ObservableList<IntegerProperty>> counts =
        FXCollectors.reduceList(numbers, FXCollectors.groupBy(group, toList()));
    assertEquals(1, counts.size());
    assertEquals(4, counts.get(0).size());

    p1.set(1);
    p2.set(2);
    p3.set(3);
    p4.set(4);

    assertEquals(4, counts.size());
    assertEquals(1, counts.get(1).size());
    assertEquals(1, counts.get(2).size());
    assertEquals(1, counts.get(3).size());
    assertEquals(1, counts.get(4).size());
    assertFalse(counts.containsKey(0));

    p4.set(1);
    assertFalse(counts.containsKey(4));
    assertEquals(2, counts.get(1).size());

    IntegerProperty p5 = new SimpleIntegerProperty(2);
    numbers.add(p5);
    assertEquals(2, counts.get(2).size());

    numbers.remove(p3);
    assertFalse(counts.containsKey(3));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void combineMapTest() {
    ObservableMap<Integer, Integer> m1 = FXCollections.observableHashMap();
    ObservableMap<Integer, Integer> m2 = FXCollections.observableHashMap();
    ObservableMap<Integer, Integer> m3 = FXCollections.observableHashMap();
    ObservableList<ObservableMap<Integer, Integer>> maps =
        FXCollections.observableArrayList(m1, m2);

    m1.put(1, 5);
    m1.put(2, 6);
    m1.put(3, 7);

    m2.put(1, 8);

    ObservableMap<Integer, ObservableList<Integer>> combined =
        FXCollectors.reduceList(maps, combineMaps(toList()));
    assertEquals(2, combined.get(1).size());
    assertEquals(1, combined.get(2).size());
    assertEquals(1, combined.get(3).size());

    m2.put(2, 9);
    assertEquals(2, combined.get(2).size());

    m3.put(3, 10);
    maps.add(m3);
    assertEquals(2, combined.get(3).size());

    maps.remove(m1);
    assertEquals(1, combined.get(1).size());
    assertEquals(1, combined.get(2).size());
    assertEquals(1, combined.get(3).size());

    m2.remove(1);
    assertFalse(combined.containsKey(1));

    assertThat(combined.get(3), contains(10));
    m3.put(3, 11);
    assertThat(combined.get(3), contains(11));
  }

  @Test
  public void toSetTest() {
    ObservableList<Integer> numbers = FXCollections.observableArrayList(1, 1, 2, 3, 4, 4);

    ObservableSet<Integer> distinct = FXCollectors.reduceList(numbers, toSet());
    assertEquals(4, distinct.size());
    assertThat(distinct, containsInAnyOrder(1, 2, 3, 4));

    numbers.remove((Integer) 1);
    assertEquals(4, distinct.size());

    numbers.remove((Integer) 1);
    assertEquals(3, distinct.size());
    assertFalse(distinct.contains(1));

    numbers.add(2);
    assertEquals(3, distinct.size());

    numbers.add(5);
    assertEquals(4, distinct.size());
    assertTrue(distinct.contains(5));
  }
}
