package com.dominikschreiber.underscore;

import com.dominikschreiber.underscore.java.util.function.BiFunction;
import com.dominikschreiber.underscore.java.util.function.BiPredicate;
import com.dominikschreiber.underscore.java.util.function.Consumer;
import com.dominikschreiber.underscore.java.util.function.Function;
import com.dominikschreiber.underscore.java.util.function.Predicate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
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

    // ----- _.each --------------------------------------------------------------------------------

    /**
     * <p>calls {@code function} on each value in {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.each(_.list(1, 2, 3, 4), new Consumer<Integer>() {
     *     public Void apply(Integer in) {
     *         System.out.print("." + in + " ");
     *     }
     * });
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
     * <p>creates a {@link List} of the results of applying {@code function} to all {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.map(_.list(1, 2, 3, 4), new Function<Integer, Integer>() {
     *     public Integer apply(Integer x) {
     *         return x * x;
     *     }
     * });
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
        return new _<>(_.map(mValues, function));
    }

    // ----- _.filter ------------------------------------------------------------------------------

    /**
     * <p>creates a {@link List} of all {@code values} that match {@code predicate}</p>
     * <p>i.e.</p>
     * <pre>
     * _.filter(_.list(1, 2, 3, 4), new Predicate<Integer>() {
     *     public boolean test(Integer x) {
     *         return x % 2 == 0;
     *     }
     * });
     * // => a List containing [2, 4]
     * </pre>
     * @param values the values to be filtered
     * @param predicate the predicate that must be matched by {@code values}
     * @param <In> type of the {@code values}
     * @return a List of all {@code values} that match {@code predicate}
     */
    public static <In> List<In> filter(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return Collections.emptyList();

        List<In> result = new ArrayList<>();
        for (In value : values)
            if (predicate.test(value))
                result.add(value);
        return result;
    }

    /** @see #filter(Iterable, Predicate) */
    public _<T> filter(Predicate<T> predicate) {
        return new _<>(_.filter(mValues, predicate));
    }

    // ----- _.find --------------------------------------------------------------------------------

    /**
     * <p>looks through the {@code values}, returns the first value that matches {@code predicate}.
     * Breaks once the first matching value is found (does not traverse the whole list then).</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.find(_.list(1,2,3,4,5), new Predicate<Integer>() {
     *     public boolean test(Integer x) {
     *         return x % 2 == 0;
     *     }
     * });
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
     * _.reduce(_.list(1, 2, 3, 4), new BiFunction<Integer, Integer, Integer>() {
     *     public Integer apply(Integer now, Integer accumulator) {
     *         return now + accumulator;
     *     }
     * }, 0);
     * // => 10
     * </pre>
     * <p>to make it clear that this is a {@code reduceLeft}, take this example:</p>
     * <pre>
     * _.reduce(_.list(1, 2, 3, 4), new BiFunction<Integer, Integer, Integer>() {
     *     public Integer apply(Integer now, Integer accumulator) {
     *         return now - accumulator;
     *     }
     * }, 0);
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
     * _.reject(_.list(1,2,3,4,5), new Predicate<Integer>() {
     *     public boolean test(Integer i) {
     *         return i % 2 == 0;
     *     }
     * });
     * // => [1,3,5]
     * }</pre>
     * @param values the values to be checked
     * @param predicate the predicate that indicates which values should be rejected
     * @param <In> the type of the values
     * @return a list of values that do not match {@code predicate}
     */
    public static <In> List<In> reject(Iterable<In> values, Predicate<In> predicate) {
        if (values == null) return Collections.emptyList();

        List<In> reject = new ArrayList<>();

        for (In value : values)
            if (!predicate.test(value))
                reject.add(value);

        return reject;
    }

    /** @see #reject(Iterable, com.dominikschreiber.underscore.java.util.function.Predicate) */
    public _<T> reject(Predicate<T> predicate) {
        return new _<>(_.reject(mValues, predicate));
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
     * <p>Short-circuits if it finds a passing value..</p>
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
     * _.contains(_.list("abcde", "fghij"), "c", new BiPredicate<String, String>() {
     *     // tests if any value in the haystack contains the needle
     *     public boolean test(String hay, String needle) {
     *         return hay.contains(needle);
     *     }
     * });
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

    // ----- _.groupBy -----------------------------------------------------------------------------

    public static <In, Key> Map<Key, List<In>> groupBy(Iterable<In> values, Function<In, Key> group) {
        if (values == null) return Collections.emptyMap();

        Map<Key, List<In>> result = new HashMap<>();

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

    /** @see #first(Iterable, int)  */
    public static <In> List<In> first(Iterable<In> values) {
        return _.first(values, 1);
    }

    /** @see #first(Iterable, int)  */
    public _<T> first(int n) {
        return new _<>(_.first(mValues, n));
    }

    /** @see #first(int) */
    public _<T> first() {
        return first(1);
    }

    // ----- _.initial -----------------------------------------------------------------------------

    public static <In> List<In> initial(Iterable<In> values, int n) {
        if (values == null) return Collections.emptyList();

        List<In> initial = new ArrayList<>();
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
        return new _<>(_.initial(mValues, n));
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

        List<In> last = new ArrayList<>();
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
    public static <In> List<In> last(Iterable<In> values) {
        return _.last(values, 1);
    }

    /** @see #last(Iterable, int) */
    public _<T> last(int n) {
        return new _<>(_.last(mValues, n));
    }

    /** @see #last(int) */
    public _<T> last() {
        return last(1);
    }

    // ----- _.rest --------------------------------------------------------------------------------

    public static <In> List<In> rest(Iterable<In> values, int startindex) {
        return _.last(values, _.size(values) - startindex);
    }

    public static <In> List<In> rest(Iterable<In> values) {
        return _.rest(values, 1);
    }

    public _<T> rest(int startindex) {
        return new _<>(_.rest(mValues, startindex));
    }

    public _<T> rest() {
        return rest(1);
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

    public static List<Integer> range(int start, int stop, int step) {
        if (start == stop ||
                step == 0 ||
                start > stop && step > 0) return Collections.emptyList();

        List<Integer> range = new ArrayList<>();
        BiPredicate<Integer, Integer> compare = (start < stop) ? smaller : greater;

        for (int i = start; compare.test(i, stop); i += step)
            range.add(i);

        return range;
    }

    public static List<Integer> range(int start, int stop) {
        return _.range(start, stop, 1);
    }

    public static List<Integer> range(int stop) {
        return _.range(0, stop);
    }

    // ===== ~Functions ============================================================================

    // ----- _.wrap ------------------------------------------------------------------------------

    public static <In, Out> Function<In, Out> wrap(Function<In, Out> function, Function<Function<In, Out>, Function<In, Out>> wrapper) {
        return wrapper.apply(function);
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
        for (Field field : options.getClass().getFields()) {
            if (field.get(options) != null) {
                defaults.getClass().getField(field.getName()).set(defaults, field.get(options));
            }
        }
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
    @SafeVarargs
    public static <In> List<In> list(In... values) {
        if (values == null) return Collections.emptyList();

        List<In> iterable = new ArrayList<>();
        for (In value : values) {
            iterable.add(value);
        }
        return iterable;
    }

    // ----- _.join --------------------------------------------------------------------------------

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

    public static String join(Iterable<String> values) {
        return _.join(values, ",");
    }

    public String join(final String separator) {
        return _.join(_.map(mValues, new Function<T, String>() {
            @Override
            public String apply(T t) {
                return t.toString();
            }
        }), separator);
    }

    public String join() {
        return join(",");
    }

    // ----- _.identity ----------------------------------------------------------------------------

    public static <In> Function<In, In> identity(Class<In> clazz) {
        return new Function<In, In>() {
            @Override
            public In apply(In in) {
                return in;
            }
        };
    }
}
