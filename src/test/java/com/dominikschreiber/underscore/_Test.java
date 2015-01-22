package com.dominikschreiber.underscore;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void chainedEachWithStringBuilder() {
        final StringBuilder result = new StringBuilder();
        new _<Integer>(range(6))
                .each(new Fn<Integer, Void>() {
                    public Void apply(Integer input) {
                        result.append(Integer.toString(input, 10));
                        return null;
                    }
                });
        assertEquals("12345", result.toString());
    }

    @Test
    public void staticMapWithSquare() {
        List<Integer> result = _.map(range(6), square);

        assertEquals(Arrays.asList(new Integer[] {1, 4, 9, 16, 25}), result);
    }

    @Test
    public void chainedMapWithSquare() {
        Iterable<Integer> result = new _<Integer>(range(6))
                .map(square)
                .value();

        assertEquals(Arrays.asList(new Integer[] {1, 4, 9, 16, 25}), result);
    }

    @Test
    public void staticFilterWithIsEven() {
        List<Integer> result = _.filter(range(6), isEven);

        assertEquals(Arrays.asList(new Integer[] {2, 4}), result);
    }

    @Test
    public void chainedFilterWithIsEven() {
        Iterable<Integer> result = new _<Integer>(range(6))
                .filter(isEven)
                .value();

        assertEquals(Arrays.asList(new Integer[] {2, 4}), result);
    }

    @Test
    public void staticReduceWithSum() {
        Integer result = _.reduce(range(6), sum, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }

    @Test
    public void chainedReduceWithSum() {
        Integer result = new _<Integer>(range(6))
                .reduce(sum, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }

    @Test
    public void chainedComplexWithSumOfEvenSquares() {
        Integer sumOfEvenSquares = new _<Integer>(range(11))
                .map(square)
                .filter(isEven)
                .reduce(sum, 0);

        assertTrue(4 + 16 + 36 + 64 + 100 == sumOfEvenSquares);
    }

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
}
