package com.dominikschreiber.underscore;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

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
     * // compute sum of squares
     * new _<Integer>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5}))
     *   // square the input
     *   .map(new Fn<Integer, Integer>() {
     *       public Integer apply(Integer in) {
     *           return in * in;
     *       }
     *   })
     *   // sum the squares
     *   .reduce(new BinaryFn<Integer, Integer, Integer>() {
     *       public Integer apply(Integer now, Integer prev) {
     *           return now + prev;
     *       }
     *   }, 0);
     * }</pre>
     * <p>If the call doesn't end up with {@link #reduce(BinaryFn, Object)},
     * you need to access the values using {@link #value()}:</p>
     * <pre>{@code
     * new _<Integer>(Arrays.asList(new Integer[] {1, 2, 3, 4, 5}))
     *   [...]
     *   .value(); // => Iterable
     * }</pre>
     * @param values the values that should be wrapped
     */
    public _(Iterable<T> values) {
        mValues = values;
    }

    public Iterable<T> value() {
        return mValues;
    }

    /**
     * <p>calls {@code function} on each value in {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.each(Arrays.asList(new Integer[] {1, 2, 3, 4}), new Fn<Integer, Void>() {
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
    public static <In> void each(Iterable<In> values, Fn<In, Void> function) {
        for (In value : values)
            function.apply(value);
    }

    /** @see #each(Iterable, Fn) */
    public _<T> each(Fn<T, Void> function) {
        _.each(mValues, function);
        return this;
    }

    /**
     * <p>creates a {@link List} of the results of applying {@code function} to all {@code values}</p>
     * <p>i.e.</p>
     * <pre>
     * _.map(Arrays.asList(new Integer[] {1, 2, 3, 4}), new Fn<Integer, Integer>() {
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
    public static <In, Out> List<Out> map(Iterable<In> values, Fn<In, Out> function) {
        List<Out> result = new LinkedList<Out>();
        for (In value : values)
            result.add(function.apply(value));
        return result;
    }

    /** @see #map(Iterable, Fn) */
    public <Out> _<Out> map(Fn<T, Out> function) {
        return new _<Out>(_.map(mValues, function));
    }

    /**
     * <p>creates a {@link List} of all {@code values} that match {@code predicate}</p>
     * <p>i.e.</p>
     * <pre>
     * _.filter(Arrays.asList(new Integer[] {1, 2, 3, 4}), new Fn<Integer, Boolean>() {
     *     public Boolean apply(Integer x) {
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
    public static <In> List<In> filter(Iterable<In> values, Fn<In, Boolean> predicate) {
        List<In> result = new LinkedList<In>();
        for (In value : values)
            if (predicate.apply(value))
                result.add(value);
        return result;
    }

    /** @see #filter(Iterable, Fn) */
    public _<T> filter(Fn<T, Boolean> predicate) {
        return new _<T>(_.filter(mValues, predicate));
    }

    /**
     * <p>reduces the {@code values} to a single value of type {@code <Out>}</p>
     * <p>(this is also known as {@code foldl}, {@code reducel}, {@code foldLeft} or {@code reduceLeft}</p>
     * <p>i.e.</p>
     * <pre>
     * _.reduce(Arrays.asList(new Integer[] {1, 2, 3, 4}), new BinaryFn<Integer, Integer, Integer>() {
     *     public Integer apply(Integer a, Integer b) {
     *         return a + b;
     *     }
     * }, 0);
     * // => 10
     * </pre>
     * <p>to make it clear that this is a {@code foldl}, take this example:</p>
     * <pre>
     * _.reduce(Arrays.asList(new Integer[] {1, 2, 3, 4}), new BinaryFn<Integer, Integer, Integer>() {
     *     public Integer apply(Integer a, Integer b) {
     *         return a - b;
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
    public static <In, Out> Out reduce(Iterable<In> values, BinaryFn<In, Out, Out> combine, Out init) {
        Out result = init;

        for (In value : values)
            result = combine.apply(value, result);

        return result;
    }

    /** @see #reduce(Iterable, BinaryFn, Object) */
    public <Out> Out reduce(BinaryFn<T, Out, Out> combine, Out init) {
        return _.reduce(mValues, combine, init);
    }

    /**
     * <p>returns {@code true} if the {@code needle} is present in {@code haystack}.</p>
     * <p>Uses {@code Object.equals()} to determine equality.</p>
     * @param haystack the values that should contain {@code needle}
     * @param needle the value to be found in {@code haystack}
     * @param <In> the type of values in haystack/needle
     * @return {@code true} if {@code needle} is found in {@code haystack}
     */
    public static <In> boolean contains(Iterable<In> haystack, In needle) {
        return _.contains(haystack, needle, new BinaryFn<In, In, Boolean>() {
            @Override
            public Boolean apply(In a, In b) {
                return a.equals(b);
            }
        });
    }

    /**
     * <p>returns {@code true} if the {@code needle} is present in {@code haystack}.</p>
     * <p>uses {@code equals} to determine equality.</p>
     * <p>e.g.</p>
     * <pre>{@code
     * _.contains(Arrays.asList(new String[] {"abcde", "fghij"}), "c", new BinaryFn<String, String, Boolean>() {
     *     // tests if any value in the haystack contains the needle
     *     public Boolean apply(String hay, String needle) {
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
    public static <In> boolean contains(Iterable<In> haystack, In needle, BinaryFn<In, In, Boolean> equals) {
        for (In hay : haystack) {
            if (equals.apply(hay, needle)) {
                return true;
            }
        }
        return false;
    }

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
}
