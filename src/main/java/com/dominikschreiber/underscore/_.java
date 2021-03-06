package com.dominikschreiber.underscore;

import com.dominikschreiber.underscore.java.util.function.BiFunction;
import com.dominikschreiber.underscore.java.util.function.BiPredicate;
import com.dominikschreiber.underscore.java.util.function.Consumer;
import com.dominikschreiber.underscore.java.util.function.Function;
import com.dominikschreiber.underscore.java.util.function.Predicate;
import com.dominikschreiber.underscore.java.util.function.Supplier;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>implements basic functional programming methods known from underscore.js</p>
 * <p><b>WARNING:</b> does not make use of advanced functional programming technologies
 * (like <i>tail calls</i> or <i>map optimization</i>) so might be slow on large data sets</p>
 */
public final class _ <T> {

    private Iterable<T> mValues;

    /**
     * <p>wraps {@code values} to allow chained execution, e.g.</p>
     * <pre>{@code
     * // compute sum of even squares
     * new _<>(_.list(1, 2, 3, 4, 5))
     *   // square the input
     *   .map(new Function<Integer, Integer>() {
     *       public Integer apply(Integer in) {
     *           return in * in;
     *       }
     *   })
     *   // pick only even squares
     *   .filter(new Predicate<Integer>() {
     *       public boolean test(Integer in) {
     *           return in % 2 == 0;
     *       }
     *   })
     *   // sum the squares
     *   .reduce(new BiFunction<Integer, Integer, Integer>() {
     *       public Integer apply(Integer now, Integer accumulator) {
     *           return now + accumulator;
     *       }
     *   }, 0);
     * }</pre>
     * <p>To get the the actual value of the computation, use {@link #value()}:</p>
     * <pre>{@code
     * new _<>(_.list(1, 2, 3, 4, 5))
     *   [...]
     *   .value(); // => Iterable<>
     * }</pre>
     * @param values the values that should be wrapped
     */
    public _(Iterable<T> values) {
        mValues = values;
    }

    public Iterable<T> value() {
        return mValues;
    }

    // ----- _.tap ---------------------------------------------------------------------------------

    public _<T> tap(Consumer<T> function) {
        _.each(mValues, function);
        return this;
    }

    // ----- _.each --------------------------------------------------------------------------------

    /**
     * <p>calls {@code function} on each value in {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.each(_.list(1, 2, 3, 4), (in) -> { System.out.print("." + in + " "); });
     * // => .1 .2 .3 .4
     * </pre>
     * @param values the values the function is called on
     * @param function the function to call on every element of {@code values}
     * @param <In> type of the elements in {@code values}
     */
    public static <In> void each(Iterable<In> values, Consumer<In> function) {
        if (values == null) return;

        for (In value : values)
            function.accept(value);
    }

    /** @see #each(Iterable, Consumer) */
    public _<T> each(Consumer<T> function) {
        _.each(mValues, function);
        return this;
    }

    // ----- _.map ---------------------------------------------------------------------------------

    /**
     * <p>creates a {@link Map} of the results of applying {@code function} to all entries of {@code values}</p>
     * <p>i.e.</p>
     * <pre>{@code
     * Map<String, Integer> lengths = new HashMap<>();
     * lengths.put("foo", 3);
     * lengths.put("quux", 4);
     * _.map(lengths, (key, value) -> { return AbstractMap.SimpleEntry(value, key); });
     * // => reverses lengths ({3: "foo", 4: "quux"})
     * }</pre>
     * @param values InKey->InValue mappings
     * @param function to be applied to (key, value) returning a new map entry
     * @param <InKey> the type of keys of {@code values}
     * @param <InValue> the type of values of {@code values}
     * @param <OutKey> the type of keys the resulting map will have
     * @param <OutValue> the type of values the resulting map will have
     * @return OutKey->OutValue mappings produced by applying {@code function} to all entries of {@code value}
     */
    public static <InKey, InValue, OutKey, OutValue> Map<OutKey, OutValue> map(Map<InKey,InValue> values, BiFunction<InKey,InValue,Map.Entry<OutKey,OutValue>> function) {
        if (values == null) return Collections.emptyMap();

        Map<OutKey,OutValue> result = new HashMap<OutKey,OutValue>();
        for (Map.Entry<InKey,InValue> value : values.entrySet()) {
            final Map.Entry<OutKey, OutValue> entry = function.apply(value.getKey(), value.getValue());
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    /**
     * <p>creates a {@link List} of the results of applying {@code function} to all {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.map(_.list(1, 2, 3, 4), (x) -> { return x * x; });
     * // => a List containing [1, 4, 9, 16]
     * </pre>
     * @param values the values to be mapped with the call of {@code function}
     * @param function the function to call on every element of {@code values}
     * @param <In> type of the elements in {@code values}
     * @param <Out> type of the result of {@code function}
     * @return a List of values of type {@code <Out>}, where {@code function} is
     * applied to all elements of {@code values}
     */
    public static <In, Out> List<Out> map(Iterable<In> values, Function<In, Out> function) {
        if (values == null) return Collections.emptyList();

        List<Out> result = new ArrayList<Out>();
        for (In value : values)
            result.add(function.apply(value));

        return result;
    }

    /** @see #map(Iterable, Function) */
    public <Out> _<Out> map(Function<T, Out> function) {
        return new _<Out>(_.map(mValues, function));
    }

    // ----- _.filter ------------------------------------------------------------------------------

    /**
     * <p>creates a {@link List} of all {@code values} that match {@code predicate}</p>
     * <p>i.e.</p>
     * <pre>
     * _.filter(_.list(1, 2, 3, 4), (x) -> { return x % 2 == 0; });
     * // => a List containing [2, 4]
     * </pre>
     * @param values the values to be filtered
     * @param predicate the predicate that must be matched by {@code values}
     * @param <In> type of the {@code values}
     * @return a List of all {@code values} that match {@code predicate}
     */
    public static <In> List<In> filter(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return Collections.emptyList();

        List<In> result = new ArrayList<In>();
        for (In value : values)
            if (predicate.test(value))
                result.add(value);
        return result;
    }

    /** @see #filter(Iterable, Predicate) */
    public _<T> filter(Predicate<T> predicate) {
        return new _<T>(_.filter(mValues, predicate));
    }

    // ----- _.find --------------------------------------------------------------------------------

    /**
     * <p>looks through the {@code values}, returns the first value that matches {@code predicate}.
     * Breaks once the first matching value is found (does not traverse the whole list then).</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.find(_.list(1,2,3,4,5), (x) -> { return x % 2 == 0; });
     * // => 2
     * }</pre>
     * @param values
     * @param predicate
     * @param <In>
     * @return
     */
    public static <In> In find(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return null;

        for (In value : values)
            if (predicate.test(value))
                return value;
        return null;
    }

    /** @see #find(Iterable, Predicate) */
    public T find(Predicate<T> predicate) {
        return _.find(mValues, predicate);
    }

    // ----- _.reduce ------------------------------------------------------------------------------

    /**
     * <p>reduces the {@code values} to a single value of type {@code <Out>}</p>
     * <p>(this is also known as {@code foldl}, {@code reducel}, {@code foldLeft} or {@code reduceLeft}</p>
     * <p>i.e.</p>
     * <pre>
     * _.reduce(_.list(1, 2, 3, 4), (now, accumulator) -> { return now + accumulator; }, 0);
     * // => 10
     * </pre>
     * <p>to make it clear that this is a {@code reduceLeft}, take this example:</p>
     * <pre>
     * _.reduce(_.list(1, 2, 3, 4), (now, accumulator) -> { return now - accumulator; }, 0);
     * // => -10 (as (0 - (1 - (2 - (3 - (4))))))
     * // not -2 (foldr would create (4 - (3 - (2 - (1 - (0))))))
     * </pre>
     * @param values the values to be reduced
     * @param combine the combination function
     * @param init the initial value (if values is empty, this is the result)
     * @param <In> the type of the values
     * @param <Out> the result type
     * @return a value of type {@code <Out>} obtained by reducing {@code values} using {@combine} to a single value
     */
    public static <In, Out> Out reduce(Iterable<In> values, BiFunction<In, Out, Out> combine, Out init) {
        if (values == null) return init;

        Out result = init;

        for (In value : values)
            result = combine.apply(value, result);

        return result;
    }

    /** @see #reduce(Iterable, BiFunction, Object) */
    public <Out> Out reduce(BiFunction<T, Out, Out> combine, Out init) {
        return _.reduce(mValues, combine, init);
    }

    // ----- _.reject ------------------------------------------------------------------------------

    /**
     * <p>Returns the {@code values} that <b>do not pass</b> the {@code predicate}.</p>
     * <p>This is the opposite of {@link #filter(Iterable, com.dominikschreiber.underscore.java.util.function.Predicate)}. E.g.:</p>
     * <pre>{@code
     * _.reject(_.list(1,2,3,4,5), (i) -> { return i % 2 == 0; });
     * // => [1,3,5]
     * }</pre>
     * @param values the values to be checked
     * @param predicate the predicate that indicates which values should be rejected
     * @param <In> the type of the values
     * @return a list of values that do not match {@code predicate}
     */
    public static <In> List<In> reject(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return Collections.emptyList();

        List<In> reject = new ArrayList<In>();

        for (In value : values)
            if (!predicate.test(value))
                reject.add(value);

        return reject;
    }

    /** @see #reject(Iterable, com.dominikschreiber.underscore.java.util.function.Predicate) */
    public _<T> reject(Predicate<T> predicate) {
        return new _<T>(_.reject(mValues, predicate));
    }

    // ----- _.every -------------------------------------------------------------------------------

    /**
     * <p>Returns {@code true} if all of the {@code values} pass {@code predicate}.</p>
     * <p>Short-circuits if it finds a non-passing value.</p>
     * @param values the values to be tested against {@code predicate}
     * @param predicate the predicate all {@code values} must pass
     * @param <In> the type of the {@code values}
     * @return {@code true} if all {@code values} pass {@code predicate}, otherwise {@code false}.
     * {@code true} if {@code values == null}.
     */
    public static <In> boolean every(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return true;

        for (In value : values)
            if (!predicate.test(value))
                return false;

        return true;
    }

    /** @see #every(Iterable, com.dominikschreiber.underscore.java.util.function.Predicate) */
    public boolean every(Predicate<T> predicate) {
        return _.every(mValues, predicate);
    }

    // ----- _.some --------------------------------------------------------------------------------

    /**
     * <p>Returns {@code true} if any of the {@code values} pass {@code predicate}.</p>
     * <p>Short-circuits if it finds a passing value.</p>
     * @param values the values to be tested against {@code predicate}
     * @param predicate the predicate that must be passed by a value in {@code values}
     * @param <In> the type of the {@code values}
     * @return {@code true} if a value in {@code values} passes {@code predicate}, otherwise {@code false}.
     * {@code false} if {@code values == null}.
     */
    public static <In> boolean some(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return false;

        for (In value : values)
            if (predicate.test(value))
                return true;

        return false;
    }

    /** @see #some(Iterable, Predicate) */
    public boolean some(Predicate<T> predicate) {
        return _.some(mValues, predicate);
    }

    // ----- _.contains ----------------------------------------------------------------------------

    /**
     * <p>returns {@code true} if the {@code needle} is present in {@code haystack}.</p>
     * <p>Uses {@code Object.equals()} to determine equality.</p>
     * @param haystack the values that should contain {@code needle}
     * @param needle the value to be found in {@code haystack}
     * @param <In> the type of values in haystack/needle
     * @return {@code true} if {@code needle} is found in {@code haystack}
     */
    public static <In> boolean contains(Iterable<In> haystack, In needle) {
        return _.contains(haystack, needle, new BiPredicate<In, In>() {
            @Override
            public boolean test(In a, In b) {
                return a.equals(b);
            }
        });
    }

    /** @see #contains(Iterable, Object) */
    public boolean contains(T needle) {
        return _.contains(mValues, needle);
    }

    /**
     * <p>returns {@code true} if the {@code needle} is present in {@code haystack}.</p>
     * <p>uses {@code equals} to determine equality.</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.contains(_.list("abcde", "fghij"), "c", (hay, needle) -> { return hay.contains(needle); });
     * // => true ("abcde" contains "c")
     * }</pre>
     * @param haystack the values that should contain {@code needle}
     * @param needle the value to be found in {@code haystack}
     * @param equals the operation that determines if a value equals {@code needle}.
     *               First parameter is the value from {@code haystack}, second parameter
     *               is the {@code needle}.
     * @param <In> the type of values in haystack/needle
     * @return {@code true} if {@code needle} is found in {@code haystack}
     */
    public static <In> boolean contains(Iterable<In> haystack, In needle, BiPredicate<In, In> equals) {
        if (haystack == null) return false;

        for (In hay : haystack) {
            if (equals.test(hay, needle)) {
                return true;
            }
        }
        return false;
    }

    /** @see #contains(Iterable, Object, BiPredicate) */
    public boolean contains(T needle, BiPredicate<T, T> equals) {
        return _.contains(mValues, needle, equals);
    }

    // ----- _.sortBy ------------------------------------------------------------------------------

    /**
     * <p>sorts  {@code values} by {@code criterion}. e.g.</p>
     * <pre>{@code
     * _.sortBy(_.list("never", "gon", "na", "give"), (i) -> { return i.length(); });
     * // => ["na", "gon", "give", "never"]
     * }</pre>
     * @param values the values to be sorted
     * @param criterion the criterion to be applied to each value to sort them
     * @param <In> the type of {@code values}
     * @return the sorted list of {@code values}
     */
    public static <In> List<In> sortBy(Iterable<In> values, final Function<In, Long> criterion) {
        if (values == null) return Collections.emptyList();

        List<In> sorted = new ArrayList<In>();
        for (In value : values)
            sorted.add(value);

        Collections.sort(sorted, new Comparator<In>() {
            @Override
            public int compare(In o1, In o2) {
                return Long.valueOf(criterion.apply(o1)).compareTo(Long.valueOf(criterion.apply(o2)));
            }
        });

        return sorted;
    }

    /** @see #sortBy(Iterable, Function) */
    public _<T> sortBy(final Function<T, Long> criterion) {
        return new _<T>(_.sortBy(mValues, criterion));
    }

    // ----- _.groupBy -----------------------------------------------------------------------------

    public static <In, Key> Map<Key, List<In>> groupBy(Iterable<In> values, Function<In, Key> group) {
        if (values == null) return Collections.emptyMap();

        Map<Key, List<In>> result = new HashMap<Key, List<In>>();

        for (In value : values) {
            Key key = group.apply(value);
            if (result.containsKey(key)) {
                result.get(key).add(value);
            } else {
                result.put(key, _.list(value));
            }
        }

        return result;
    }

    public <Key> Map<Key, List<T>> groupBy(Function<T, Key> group) {
        return _.groupBy(mValues, group);
    }

    // ----- _.size --------------------------------------------------------------------------------

    /**
     * <p>returns the number of values in {@code values}</p>
     * @param values the values to be counted
     * @return the number of values
     */
    public static <In> int size(Iterable<In> values) {
        if (values == null) return 0;

        int size = 0;
        for (In value : values) {
            size += 1;
        }
        return size;
    }

    /** @see #size(Iterable) */
    public int size() {
        return _.size(mValues);
    }

    // ----- _.first -------------------------------------------------------------------------------

    /**
     * <p>returns the first {@code n} elements of {@code values}</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.first(_.list(1, 2, 3, 4, 5), 2);
     * // => [1, 2]
     * }</pre>
     * @param values
     * @param n
     * @param <In>
     * @return
     */
    public static <In> List<In> first(Iterable<In> values, int n) {
        if (values == null) return Collections.emptyList();

        List<In> first = new ArrayList<In>(n);
        Iterator<In> iterator = values.iterator();

        for (int i = 0; i < n && iterator.hasNext(); i++) {
            first.add(iterator.next());
        }

        return first;
    }

    /**
     * <p>returns the first element of {@code values} </p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.first(_.list("never","gonna","give","you","up"));
     * // => "never"
     * }</pre>
     */
    public static <In> In first(Iterable<In> values) {
        if (values == null || !values.iterator().hasNext()) return null;
        return values.iterator().next();
    }

    /** @see #first(Iterable, int)  */
    public _<T> first(int n) {
        return new _<T>(_.first(mValues, n));
    }

    /** @see #first(Iterable) */
    public T first() {
        return _.first(mValues);
    }

    // ----- _.initial -----------------------------------------------------------------------------

    public static <In> List<In> initial(Iterable<In> values, int n) {
        if (values == null) return Collections.emptyList();

        List<In> initial = new ArrayList<In>();
        Iterator<In> iterator = values.iterator();
        int limit = _.size(values) - n;

        for (int i = 0; i < limit && iterator.hasNext(); i++) {
            initial.add(iterator.next());
        }

        return initial;
    }

    public static <In> List<In> initial(Iterable<In> values) {
        return _.initial(values, 1);
    }

    public _<T> initial(int n) {
        return new _<T>(_.initial(mValues, n));
    }

    public _<T> initial() {
        return initial(1);
    }

    // ----- .last ---------------------------------------------------------------------------------

    /**
     * <p>returns the last {@code n} elements of {@code values}</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.last(_.list("foo", "bar", "baz"), 2);
     * // => ["bar", "baz"]
     * }</pre>
     * @param values the values to take the last {@code n} from
     * @param n the number of values to take from {@code values}, defaults to 1
     * @return the last {@code n} {@code values}
     */
    public static <In> List<In> last(Iterable<In> values, int n) {
        if (values == null) return Collections.emptyList();

        List<In> last = new ArrayList<In>();
        int limit = _.size(values) - n;

        int i = 0;
        for (In value : values) {
            if (i >= limit) {
                last.add(value);
            }
            i += 1;
        }

        return last;
    }

    /** @see #last(Iterable, int) */
    public static <In> In last(Iterable<In> values) {
        Iterator<In> iterator = values.iterator();
        In last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }
        return last;
    }

    /** @see #last(Iterable, int) */
    public _<T> last(int n) {
        return new _<T>(_.last(mValues, n));
    }

    /** @see #last(int) */
    public T last() {
        return _.last(mValues);
    }

    // ----- _.rest --------------------------------------------------------------------------------

    public static <In> List<In> rest(Iterable<In> values, int startindex) {
        return _.last(values, _.size(values) - startindex);
    }

    public static <In> List<In> rest(Iterable<In> values) {
        return _.rest(values, 1);
    }

    public _<T> rest(int startindex) {
        return new _<T>(_.rest(mValues, startindex));
    }

    public _<T> rest() {
        return rest(1);
    }

    // ----- _.zip ---------------------------------------------------------------------------------

    /**
     * <p>returns a merged list of the lists {@code first} and {@code second}. E.g.</p>
     * <pre>{@code
     * _.zip(_.list(1,2,3), _.list(4,5,6)) // => [(1,4), (2,5), (3,6)]
     * }</pre>
     * <p>if the lists have different lengths, the merged list will be as long as the shorter list. I.e.</p>
     * <pre>{@code
     * _.zip(_.list(1,2,3), _.list(4,5,6,7,8,9)) // => [(1,4), (2,5), (3,6)]
     * }</pre>
     * @param first the first list of values
     * @param second the second list of values
     * @param <F> the type of values in {@code first}
     * @param <S> the type of values in {@code second}
     * @return the merged list
     */
    public static <F,S> List<Map.Entry<F,S>> zip(Iterable<F> first, Iterable<S> second) {
        if (first == null || second == null) return Collections.emptyList();

        List<Map.Entry<F,S>> zipped = new ArrayList<Map.Entry<F,S>>();
        Iterator<F> f;
        Iterator<S> s;

        for (f = first.iterator(), s = second.iterator(); f.hasNext() && s.hasNext();/* nothing */) {
            zipped.add(_.entry(f.next(), s.next()));
        }

        return zipped;
    }

    /** @see #zip(Iterable, Iterable) */
    public <O> _<Map.Entry<T,O>> zip(Iterable<O> other) {
        return new _<Map.Entry<T,O>>(_.zip(mValues, other));
    }

    // ----- _.range -------------------------------------------------------------------------------

    private static BiPredicate<Integer, Integer> greater = new BiPredicate<Integer, Integer>() {
        @Override
        public boolean test(Integer a, Integer b) {
            return a > b;
        }
    };
    private static BiPredicate<Integer, Integer> smaller = new BiPredicate<Integer, Integer>() {
        @Override
        public boolean test(Integer a, Integer b) {
            return a < b;
        }
    };

    /**
     * <p>returns a list of integers starting at {@code start}, ending at {@code stop},
     * incrementing by {@code step}. E.g.</p>
     * <pre>{@code
     * _.range(5) // => [0, 1, 2, 3, 4]
     * _.range(3, 9) // => [3, 4, 5, 6, 7, 8]
     * _.range(5, 16, 2) // => [5, 7, 9, 11, 13, 15]
     * _.range(0, -4, -1) // => [0, -1, -2, -3]
     * }</pre>
     * <p>Negative ranges without negative step are considered empty:</p>
     * <pre>{@code
     * _.range(3, 2, 1) // => []
     * }</pre>
     * @param start the start index, included (defaults to {@code 0})
     * @param stop the end index, excluded
     * @param step the stepwidth (defaults to {@code 1})
     * @return a list of integers in the specified range
     */
    public static List<Integer> range(int start, int stop, int step) {
        if (start == stop ||
                step == 0 ||
                start > stop && step > 0) return Collections.emptyList();

        List<Integer> range = new ArrayList<Integer>();
        BiPredicate<Integer, Integer> compare = (start < stop) ? smaller : greater;

        for (int i = start; compare.test(i, stop); i += step)
            range.add(i);

        return range;
    }

    /** @see #range(int, int, int) */
    public static List<Integer> range(int start, int stop) {
        return _.range(start, stop, 1);
    }

    /** @see #range(int, int, int) */
    public static List<Integer> range(int stop) {
        return _.range(0, stop);
    }

    // ===== ~Functions ============================================================================

    // ----- _.wrap --------------------------------------------------------------------------------

    public static <In, Out> Function<In, Out> wrap(Function<In, Out> function, Function<Function<In, Out>, Function<In, Out>> wrapper) {
        return wrapper.apply(function);
    }

    // ----- _.negate ------------------------------------------------------------------------------

    /**
     * <p>returns the negated version of {@code predicate}. I.e.</p>
     * <pre>forall x of In: predicate(x) XOR _.negate(predicate)(x)</pre>
     * @param predicate the predicate to be negated
     * @return the negated predicate
     */
    public static <In> Predicate<In> negate(final Predicate<In> predicate) {
        return new Predicate<In>() {
            @Override
            public boolean test(In in) {
                return !predicate.test(in);
            }
        };
    }

    /** @see #negate(Predicate) */
    public static <U, V> BiPredicate<U, V> negate(final BiPredicate<U, V> predicate) {
        return new BiPredicate<U, V>() {
            @Override
            public boolean test(U u, V v) {
                return !predicate.test(u,v);
            }
        };
    }

    // ===== ~Objects ==============================================================================

    // ----- _.extend ------------------------------------------------------------------------------

    /**
     * <p>extends public fields in {@code defaults} with values in {@code options} if they are not {@code null}.</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.extend(datastore(0, 1), datastore(null, 2));
     * // => datastore(0, 2);
     * // with definitions
     * class Datastore {
     *     public Integer a;
     *     public Integer b;
     * }
     * datastore(Integer a, Integer b) {
     *     Datastore result = new Datastore();
     *     result.a = a;
     *     result.b = b;
     *     return result;
     * }
     * }</pre>
     * <p>alters values of {@code defaults}, does not create a copy and work on it</p>
     * @param defaults
     * @param options
     * @param <Datastore>
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static <Datastore> Datastore extend(Datastore defaults, Datastore options) throws IllegalAccessException, NoSuchFieldException {
        for (Field field : options.getClass().getFields())
            if (field.get(options) != null)
                defaults.getClass().getField(field.getName()).set(defaults, field.get(options));

        return defaults;
    }

    // ===== Utility ===============================================================================

    // ----- _.list --------------------------------------------------------------------------------

    /**
     * <p>Creates a list of the values passed as arguments. E.g.</p>
     * <pre>{@code
     * _.list("foo", "bar", "baz")
     * // => ["foo", "bar", "baz"]
     * }</pre>
     * <p>Without this function one would have written something like</p>
     * <pre>{@code
     * Arrays.asList(new String[] {"foo", "bar", "baz"})
     * // => ["foo", "baz", "baz"]
     * }</pre>
     * <p>what would not allow e.g. adding items to the list</p>
     * @param values the values to create a list from
     * @param <In> the type the items in the list will have
     * @return a list that contains exactly the values
     */
    public static <In> List<In> list(In... values) {
        if (values == null) return Collections.emptyList();

        List<In> iterable = new ArrayList<In>();
        for (In value : values) {
            iterable.add(value);
        }
        return iterable;
    }

    // ----- _.dictionary --------------------------------------------------------------------------

    /**
     * <p>creates a map from the list of map {@code entries}</p>
     * <pre>{@code
     * _.dictionary(
     *     new AbstractMap.SimpleEntry("foo", 3),
     *     new AbstractMap.SimpleEntry("quux", 4)
     * );
     * // => {"foo": 3, "quux": 4}
     * }</pre>
     * <p>(_.map would clash with this method => is named _.dictionary instead)</p>
     * @param entries list of map entries
     * @param <Key> type of the keys
     * @param <Value> type of the values
     * @return a map created from the list of map entries
     */
    public static <Key,Value> Map<Key,Value> dictionary(Map.Entry<Key, Value>... entries) {
        if (entries == null) return Collections.emptyMap();

        Map<Key,Value> result = new HashMap<Key,Value>();
        for (Map.Entry<Key, Value> entry : entries)
            result.put(entry.getKey(), entry.getValue());

        return result;
    }

    // ----- _.entry -------------------------------------------------------------------------------

    public static <Key,Value> Map.Entry<Key,Value> entry(Key key, Value value) {
        return new AbstractMap.SimpleEntry<Key,Value>(key, value);
    }

    // ----- _.join --------------------------------------------------------------------------------

    /**
     * <p>Joins the {@code values} using the specified {@code separator}. E.g.</p>
     * <pre>{@code
     * _.join(_.list("foo", "bar"), "<->") // => "foo<->bar"
     * }</pre>
     * @param values the Strings to be joined
     * @param separator the separator to be used (defaults to {@code ","})
     * @return the joined values
     */
    public static String join(Iterable<String> values, final String separator) {
        if (values == null) return "";

        StringBuilder joined = new StringBuilder();
        boolean isFirst = true;

        for (String value : values) {
            if (isFirst) {
                joined.append(value);
                isFirst = false;
            } else {
                joined.append(separator).append(value);
            }
        }

        return joined.toString();
    }

    /** @see #join(Iterable, String) */
    public static String join(Iterable<String> values) {
        return _.join(values, ",");
    }

    /** @see #join(Iterable, String) */
    public String join(final String separator) {
        return _.join(_.map(mValues, new Function<T, String>() {
            @Override
            public String apply(T t) {
                return t.toString();
            }
        }), separator);
    }

    /** @see #join(Iterable, String) */
    public String join() {
        return join(",");
    }

    // ----- _.stringify ---------------------------------------------------------------------------

    public static String stringify(Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof String) {
            String s = o.toString();
            if ((s.startsWith("[") && s.endsWith("]"))
                || (s.startsWith("{") && s.endsWith("}"))) {
                return s;
            } else {
                return '"' + s + '"';
            }
        }
        if (o instanceof Iterable) {
            return '[' + new _((Iterable) o)
                    .map(new Function<Object, String>() {
                        @Override
                        public String apply(Object o) {
                            return _.stringify(o);
                        }
                    })
                    .join() + ']';
        }
        if (o instanceof Map) {
            return '{' + new _(((Map) o).entrySet())
                    .map(new Function<Map.Entry, String>() {
                        @Override
                        public String apply(Map.Entry o) {
                            return _.stringify(o.getKey()) + ':' + _.stringify(o.getValue());
                        }
                    })
                    .join() + '}';
        }
        return o.toString();
    }

    public String stringify() {
        return _.stringify(mValues);
    }

    // ----- _.identity ----------------------------------------------------------------------------

    /**
     * <p>returns the identity function for {@code In} values. E.g.</p>
     * <pre>{@code
     * _.identity(String.class).apply("foo") // => "foo"
     * }</pre>
     * @param clazz
     * @param <In>
     * @return
     */
    public static <In> Function<In, In> identity(Class<In> clazz) {
        return new Function<In, In>() {
            @Override
            public In apply(In in) {
                return in;
            }
        };
    }

    // ----- _.constant ----------------------------------------------------------------------------

    /**
     * <p>returns a function that always returns {@code value}. E.g.:</p>
     * <pre>{@code
     * Supplier<Integer> five = _.constant(5);
     * five.get() // => 5
     * }</pre>
     * @param value the value the constant function should return
     * @param <In> the type of the value returned by the function
     * @return a {@link Supplier} that returns {@code value}
     */
    public static <In> Supplier<In> constant(final In value) {
        return new Supplier<In>() {
            @Override
            public In get() {
                return value;
            }
        };
    }

    // ----- _.noop --------------------------------------------------------------------------------

    public static <In> Consumer<In> noop() {
        return new Consumer<In>() {
            @Override
            public void accept(In in) {
                // noop
            }
        };
    }
}
