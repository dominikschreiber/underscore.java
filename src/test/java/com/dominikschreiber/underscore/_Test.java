package com.dominikschreiber.underscore;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class _Test {
    private Fn<Integer, Integer> square = new Fn<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input * input;
        }
    };
    private Fn<Integer, Boolean> isEven = new Fn<Integer, Boolean>() {
        @Override
        public Boolean apply(Integer input) {
            return input % 2 == 0;
        }
    };
    private BinaryFn<Integer, Integer, Integer> sum = new BinaryFn<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer current, Integer sum) {
            return current + sum;
        }
    };
    private BinaryFn<String, String, Boolean> stringEquals = new BinaryFn<String, String, Boolean>() {
        @Override
        public Boolean apply(String a, String b) {
            return a.equals(b);
        }
    };

    private List<Integer> range(int from, int to) {
        List<Integer> result = new ArrayList<Integer>(to - from + 1);
        for (int i = from; i < to; i++) {
            result.add(i);
        }
        return result;
    }

    private List<Integer> range(int to) {
        return range(1, to);
    }

    // ----- _.each --------------------------------------------------------------------------------

    @Test
    public void staticEachWithStringBuilder() {
        final StringBuilder result = new StringBuilder();

        _.each(range(6), new Fn<Integer, Void>() {
            @Override
            public Void apply(Integer input) {
                result.append(Integer.toString(input, 10));
                return null;
            }
        });

        assertEquals("12345", result.toString());
    }

    @Test
    public void staticEachNullInput() {
        _.each(null, new Fn<Object, Void>() {
            @Override
            public Void apply(Object input) {
                return null;
            }
        });
        // tests pass if no exception is thrown -- no need to Assert.pass()
    }

    @Test
    public void chainedEachWithStringBuilder() {
        final StringBuilder result = new StringBuilder();
        new _<>(range(6))
                .each(new Fn<Integer, Void>() {
                    public Void apply(Integer input) {
                        result.append(Integer.toString(input, 10));
                        return null;
                    }
                });
        assertEquals("12345", result.toString());
    }

    // ----- _.map ---------------------------------------------------------------------------------

    @Test
    public void staticMapWithSquare() {
        List<Integer> result = _.map(range(6), square);

        assertEquals(_.list(1, 4, 9, 16, 25), result);
    }

    @Test
    public void staticMapWithNullInput() {
        assertEquals(Collections.emptyList(), _.map(null, square));
    }

    @Test
    public void chainedMapWithSquare() {
        Iterable<Integer> result = new _<>(range(6))
                .map(square)
                .value();

        assertEquals(Arrays.asList(new Integer[] {1, 4, 9, 16, 25}), result);
    }

    // ----- _.filter ------------------------------------------------------------------------------

    @Test
    public void staticFilterWithIsEven() {
        List<Integer> result = _.filter(range(6), isEven);

        assertEquals(Arrays.asList(new Integer[] {2, 4}), result);
    }

    @Test
    public void staticFilterWithNullInput() {
        assertEquals(Collections.emptyList(), _.filter(null, isEven));
    }

    @Test
    public void chainedFilterWithIsEven() {
        Iterable<Integer> result = new _<>(range(6))
                .filter(isEven)
                .value();

        assertEquals(_.list(2, 4), result);
    }

    // ----- _.find --------------------------------------------------------------------------------

    @Test
    public void staticFindWithIsEven() {
        assertEquals(
                2,
                (int) _.find(range(6), isEven)
        );
    }

    @Test
    public void staticFindWithNullInput() {
        assertEquals(null, _.find(null, isEven));
    }

    @Test
    public void chainedFindWithIsEven() {
        assertEquals(
                2,
                (int) new _<>(range(6)).find(isEven)
        );
    }

    // ----- _.reduce ------------------------------------------------------------------------------

    @Test
    public void staticReduceWithSum() {
        Integer result = _.reduce(range(6), sum, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }

    @Test
    public void staticReduceWithNullInput() {
        assertEquals(
                5,
                (int) _.reduce(null, sum, 5)
        );
    }

    @Test
    public void chainedReduceWithSum() {
        Integer result = new _<>(range(6))
                .reduce(sum, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }

    @Test
    public void chainedComplexWithSumOfEvenSquares() {
        Integer sumOfEvenSquares = new _<>(range(11))
                .map(square)
                .filter(isEven)
                .reduce(sum, 0);

        assertTrue(4 + 16 + 36 + 64 + 100 == sumOfEvenSquares);
    }

    // ---- _.contains -----------------------------------------------------------------------------

    @Test
    public void staticContainsWithIntegers() {
        assertTrue(_.contains(range(6), 4));
        assertFalse(_.contains(range(4), 6));
    }

    @Test
    public void staticContainsWithNullInput() {
        assertFalse(_.contains(null, "foo"));
    }

    @Test
    public void chainedContainsWithIntegers() {
        assertTrue(new _<>(range(6)).contains(4));
        assertFalse(new _<>(range(4)).contains(6));
    }

    @Test
    public void staticContainsWithStringEquals() {
        assertTrue(_.contains(_.list("foo", "bar", "baz"), "foo", stringEquals));
        assertFalse(_.contains(_.list("foo", "bar"), "baz", stringEquals));
    }

    @Test
    public void chainedContainsWithStringEquals() {
        assertTrue(new _<>(_.list("foo", "bar", "baz")).contains("foo", stringEquals));
        assertFalse(new _<>(_.list("foo", "bar")).contains("baz", stringEquals));
    }

    // ----- _.size --------------------------------------------------------------------------------

    @Test
    public void staticSize() {
        assertEquals(3, _.size(range(4)));
    }

    @Test
    public void staticSizeWithNullInput() {
        assertEquals(0, _.size(null));
    }

    @Test
    public void chainedSize() {
        assertEquals(3, new _<>(range(4)).size());
    }

    // ----- _.first -------------------------------------------------------------------------------

    @Test
    public void staticFirst() {
        assertEquals(range(4), _.first(range(5), 3));
    }

    @Test
    public void staticFirstWithNullInput() {
        assertEquals(Collections.emptyList(), _.first(null, 3));
    }

    @Test
    public void staticFirstDefaultN() {
        assertEquals(range(2), _.first(range(5)));
    }

    @Test
    public void chainedFirst() {
        assertEquals(range(4),new _<>(range(5)).first(3).value());
    }

    @Test
    public void chainedFirstDefaultN() {
        assertEquals(
                range(2),
                new _<>(range(5)).first().value()
        );
    }

    // ----- _.initial -----------------------------------------------------------------------------

    @Test
    public void staticInitial() {
        assertEquals(_.list("foo"), _.initial(_.list("foo", "bar", "baz"), 2));
    }

    @Test
    public void staticInitialWithNullInput() {
        assertEquals(Collections.emptyList(), _.initial(null, 2));
    }

    @Test
    public void staticInitialDefaultN() {
        assertEquals(_.list("foo", "bar"), _.initial(_.list("foo", "bar", "baz")));
    }

    @Test
    public void chainedInitial() {
        assertEquals(_.list("foo"), new _<>(_.list("foo", "bar", "baz")).initial(2).value());
    }

    @Test
    public void chainedInitialDefaultN() {
        assertEquals(_.list("foo", "bar"), new _<>(_.list("foo", "bar", "baz")).initial().value());
    }

    // ----- _.last --------------------------------------------------------------------------------

    @Test
    public void staticLast() {
        assertEquals(range(4, 6), _.last(range(6), 2));
    }

    @Test
    public void staticLastDefaultN() {
        assertEquals(range(4, 5), _.last(range(5)));
    }

    @Test
    public void chainedLast() {
        assertEquals(range(4, 6), new _<>(range(6)).last(2).value());
    }

    @Test
    public void chainedLastDefaultN() {
        assertEquals(range(4, 5), new _<>(range(5)).last().value());
    }

    // ----- _.rest --------------------------------------------------------------------------------

    @Test
    public void staticRest() {
        assertEquals(range(3, 5), _.rest(range(5), 2));
    }

    @Test
    public void staticRestWithNullInput() {
        assertEquals(Collections.emptyList(), _.rest(null, 2));
    }

    @Test
    public void staticRestDefaultN() {
        assertEquals(range(2, 5), _.rest(range(5)));
    }

    @Test
    public void chainedRest() {
        assertEquals(range(3, 5), new _<>(range(5)).rest(2).value());
    }

    @Test
    public void chainedRestDefaultN() {
        assertEquals(range(2, 5), new _<>(range(5)).rest().value());
    }

    // ----- _.extend ------------------------------------------------------------------------------

    @Test
    public void extendWithNothing() {
        Datastore defaults = datastore(0, 1);
        Datastore options = datastore(null, null);
        Datastore expected = datastore(defaults.a, defaults.b);

        try {
            Datastore result = _.extend(defaults, options);
            assertEquals(expected.a, result.a);
            assertEquals(expected.b, result.b);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void extendWithOverrides() {
        Datastore defaults = datastore(0, 1);
        Datastore options = datastore(2, 3);
        Datastore expected = datastore(options.a, options.b);

        try {
            Datastore result = _.extend(defaults, options);
            assertEquals(expected.a, result.a);
            assertEquals(expected.b, result.b);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void extendWithPartialOverrides() {
        Datastore defaults = datastore(0, 1);
        Datastore options = datastore(null, 3);
        Datastore expected = datastore(defaults.a, options.b);

        try {
            Datastore result = _.extend(defaults, options);
            assertEquals(expected.a, result.a);
            assertEquals(expected.b, result.b);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private Datastore datastore(Integer a, Integer b) {
        Datastore result = new Datastore();
        result.a = a;
        result.b = b;
        return result;
    }

    private static class Datastore {
        public Integer a;
        public Integer b;
    }

    @Test
    public void extendWithPartialOverridesAndMethodsInDatastore() {
        MethodDatastore defaults = methodDatastore(0, 1);
        MethodDatastore options = methodDatastore(null, 3);
        MethodDatastore expected = methodDatastore(defaults.a, options.b);

        try {
            MethodDatastore result = _.extend(defaults, options);
            assertEquals(expected.a, result.a);
            assertEquals(expected.b, result.b);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private MethodDatastore methodDatastore(Integer a, Integer b) {
        MethodDatastore result = new MethodDatastore();
        result.a = a;
        result.b = b;
        return result;
    }

    private static class MethodDatastore extends Datastore {
        public void foo() {}
    }

    @Test
    public void extendWithPartialOverridesAndBuilderInDatastore() {
        BuilderDatastore defaults = new BuilderDatastore.Builder()
                .setA(0)
                .setB(1)
                .build();
        BuilderDatastore options = new BuilderDatastore.Builder()
                .setA(null)
                .setB(2)
                .build();
        BuilderDatastore expected = new BuilderDatastore.Builder()
                .setA(defaults.a)
                .setB(options.b)
                .build();

        try {
            BuilderDatastore result = _.extend(defaults, options);
            assertEquals(expected.a, result.a);
            assertEquals(expected.b, result.b);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static class BuilderDatastore extends Datastore {
        public static class Builder {
            private BuilderDatastore store = new BuilderDatastore();
            public Builder setA(Integer a) { store.a = a; return this; }
            public Builder setB(Integer b) { store.b = b; return this; }
            public BuilderDatastore build() { return store; }
        }
    }

    // ----- _.list --------------------------------------------------------------------------------

    @Test
    public void list() {
        assertTrue(_.list("foo", "bar", "baz") instanceof List);
    }

    @Test
    public void listWithNullInput() {
        assertEquals(Collections.emptyList(), _.list());
    }
}
