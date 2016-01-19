package name.falgout.jeffrey.fx.reduce;

import static name.falgout.jeffrey.fx.reduce.FXCollectors.averagingIntegers;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.observing;
import static name.falgout.jeffrey.fx.reduce.FXCollectors.toMultiset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.LinkedHashMap;
import java.util.function.Function;

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

import com.google.common.collect.Multiset;

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
    assertFalse(average.averageProperty().isValid());
    assertFalse(average.sumProperty().isValid());
    assertFalse(average.countProperty().isValid());

    assertEquals(5, average.getCount());
    assertEquals(15, (int) average.getSum());
    assertEquals(3.0, average.getAverage().get(), 0);

    numbers.set(0, 5);
    assertTrue(average.countProperty().isValid());
    assertEquals(19, (int) average.getSum());
  }

  @Test
  public void groupBy() {
    IntegerProperty p1 = new SimpleIntegerProperty();
    IntegerProperty p2 = new SimpleIntegerProperty();
    IntegerProperty p3 = new SimpleIntegerProperty();
    IntegerProperty p4 = new SimpleIntegerProperty();
    ObservableList<IntegerProperty> numbers = FXCollections.observableArrayList(p1, p2, p3, p4);

    Function<IntegerProperty, ObservableValue<Number>> group = t -> t;
    ObservableMap<Number, Multiset<IntegerProperty>> counts =
        FXCollectors.reduceList(numbers, FXCollectors.groupBy(group, toMultiset()));
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
}
