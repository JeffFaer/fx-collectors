package name.falgout.jeffrey.fx.reduce;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public final class FXCollectors {
  private FXCollectors() {}

  public static <T, A, R> R reduce(T item, FXCollector<? super T, A, R> reduction) {
    Objects.requireNonNull(item);
    Objects.requireNonNull(reduction);

    A aggregate = reduction.supplier().get();
    reduction.add().accept(aggregate, item);
    return reduction.finisher().apply(aggregate);
  }

  public static <T, R> R reduceList(ObservableList<? extends T> list,
      FXCollector<? super T, ?, R> reduction) {
    return reduce(list, reducingLists(reduction));
  }

  public static <T, R> R reduceSet(ObservableSet<? extends T> set,
      FXCollector<? super T, ?, R> reduction) {
    return reduce(set, reducingSets(reduction));
  }

  public static <K, V, R> R reduceMap(ObservableMap<K, V> map,
      FXCollector<? super Entry<K, V>, ?, R> reduction) {
    return reduce(map, reducingMaps(reduction));
  }

  public static <K, V, R> R reduceMapKeys(ObservableMap<K, V> map,
      FXCollector<? super K, ?, R> reduction) {
    return reduce(map, reducingMapKeys(reduction));
  }

  public static <K, V, R> R reduceMapValues(ObservableMap<K, V> map,
      FXCollector<? super V, ?, R> reduction) {
    return reduce(map, reducingMapValues(reduction));
  }

  public static <T, R> FXCollector<ObservableList<T>, ?, R>
      reducingLists(FXCollector<? super T, ?, R> downstream) {
    Objects.requireNonNull(downstream);
    return new ListCollector<>(downstream);
  }

  public static <T, R> FXCollector<ObservableSet<T>, ?, R>
      reducingSets(FXCollector<? super T, ?, R> downstream) {
    Objects.requireNonNull(downstream);
    return new SetCollector<>(downstream);
  }

  public static <K, V, R> FXCollector<ObservableMap<K, V>, ?, R>
      reducingMaps(FXCollector<? super Entry<K, V>, ?, R> downstream) {
    Objects.requireNonNull(downstream);
    return new MapCollector<>(downstream);
  }

  public static <K, V, R> FXCollector<ObservableMap<K, V>, ?, R>
      reducingMapKeys(FXCollector<? super K, ?, R> downstream) {
    return reducingMaps(mapping(Entry::getKey, downstream));
  }

  public static <K, V, R> FXCollector<ObservableMap<K, V>, ?, R>
      reducingMapValues(FXCollector<? super V, ?, R> downstream) {
    return reducingMaps(mapping(Entry::getValue, downstream));
  }

  public static <T, R> FXCollector<ObservableValue<T>, ?, R>
      observing(FXCollector<? super T, ?, R> downstream) {
    Objects.requireNonNull(downstream);
    return new ValueCollector<>(downstream);
  }

  public static <T, U, R> FXCollector<T, ?, R> mapping(Function<? super T, ? extends U> map,
      FXCollector<? super U, ?, R> downstream) {
    Objects.requireNonNull(map);
    Objects.requireNonNull(downstream);
    return new MappedCollector<>(map, downstream);
  }

  public static <T, U, R> FXCollector<T, ?, R> observableMapping(
      Function<? super T, ? extends ObservableValue<U>> map,
      FXCollector<? super U, ?, R> downstream) {
    return mapping(map, observing(downstream));
  }

  public static <S extends Number, A extends Number> FXCollector<S, ?, NumberAverage<S, A>>
      averagingNumber(NumberAverage<S, A> average) {
    Objects.requireNonNull(average);
    return new NumberCollector<>(average);
  }

  public static FXCollector<Integer, ?, NumberAverage<Integer, Double>> averagingIntegers() {
    return averagingNumber(new IntegerAverage());
  }

  public static <T, G, R> FXCollector<T, ?, ObservableMap<G, R>> groupBy(
      Function<? super T, ? extends ObservableValue<G>> grouper,
      FXCollector<? super T, ?, R> downstream) {
    return groupBy(grouper, StandardMapFactory.LINKED_HASH_MAP, downstream);
  }

  public static <T, G, R> FXCollector<T, ?, ObservableMap<G, R>> groupBy(
      Function<? super T, ? extends ObservableValue<G>> grouper,
      UnaryOperator<ObservableMap<G, R>> finisher, FXCollector<? super T, ?, R> downstream) {
    return groupBy(grouper, StandardMapFactory.LINKED_HASH_MAP, finisher, downstream);
  }

  public static <T, G, R> FXCollector<T, ?, ObservableMap<G, R>> groupBy(
      Function<? super T, ? extends ObservableValue<G>> grouper, MapFactory mapFactory,
      FXCollector<? super T, ?, R> downstream) {
    return groupBy(grouper, mapFactory, FXCollections::unmodifiableObservableMap, downstream);
  }

  public static <T, G, R> FXCollector<T, ?, ObservableMap<G, R>> groupBy(
      Function<? super T, ? extends ObservableValue<G>> grouper, MapFactory mapFactory,
      UnaryOperator<ObservableMap<G, R>> finisher, FXCollector<? super T, ?, R> downstream) {
    Objects.requireNonNull(grouper);
    Objects.requireNonNull(mapFactory);
    Objects.requireNonNull(finisher);
    Objects.requireNonNull(downstream);
    return new GroupingCollector<>(grouper, mapFactory, finisher, downstream);
  }

  public static <T> FXCollector<T, ?, Multiset<T>> toMultiset() {
    return toCollection(LinkedHashMultiset::create, Multisets::unmodifiableMultiset);
  }

  public static <T, C extends Collection<T>> FXCollector<T, ?, C> toCollection(Supplier<C> ctor) {
    return toCollection(ctor, UnaryOperator.identity());
  }

  public static <T, C extends Collection<T>> FXCollector<T, ?, C> toCollection(Supplier<C> ctor,
      UnaryOperator<C> finisher) {
    Objects.requireNonNull(ctor);
    Objects.requireNonNull(finisher);
    return new ToCollectionCollector<>(ctor, finisher);
  }
}
