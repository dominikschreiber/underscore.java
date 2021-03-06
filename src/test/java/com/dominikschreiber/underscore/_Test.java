package com.dominikschreiber.underscore;

import com.dominikschreiber.underscore.java.util.function.BiFunction;
import com.dominikschreiber.underscore.java.util.function.BiPredicate;
import com.dominikschreiber.underscore.java.util.function.Consumer;
import com.dominikschreiber.underscore.java.util.function.Function;
import com.dominikschreiber.underscore.java.util.function.Predicate;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class _Test {
    private Function<Integer, Integer> square = new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer input) {
            return input * input;
        }
    };
    private Predicate<Integer> isEven = new Predicate<Integer>() {
        @Override
        public boolean test(Integer input) {
            return input % 2 == 0;
        }
    };
    private BiFunction<Integer, Integer, Integer> sum = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer current, Integer sum) {
            return current + sum;
        }
    };
    private BiPredicate<String, String> stringEquals = new BiPredicate<String, String>() {
        @Override
        public boolean test(String a, String b) {
            return a.equals(b);
        }
    };
    private Function<String, Long> length = new Function<String, Long>() {
        @Override
        public Long apply(String s) {
            return (long) s.length();
        }
    };

    // ----- _.each --------------------------------------------------------------------------------

    @Test
    public void staticEachWithStringBuilder() {
        final StringBuilder result = new StringBuilder();

        _.each(_.range(1, 6), new Consumer<Integer>() {
            @Override
            public void accept(Integer input) {
                result.append(Integer.toString(input, 10));
            }
        });

        assertEquals("12345", result.toString());
    }

    @Test
    public void staticEachNullInput() {
        _.each(null, new Consumer<Object>() {
            @Override
            public void accept(Object input) {
            }
        });
        // tests pass if no exception is thrown -- no need to Assert.pass()
    }

    @Test
    public void chainedEachWithStringBuilder() {
        final StringBuilder result = new StringBuilder();
        new _<Integer>(_.range(1, 6))
                .each(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer input) {
                        result.append(Integer.toString(input, 10));
                    }
                });
        assertEquals("12345", result.toString());
    }

    // ----- _.map ---------------------------------------------------------------------------------

    @Test
    public void staticMapWithSquare() {
        List<Integer> result = _.map(_.range(1, 6), square);

        assertEquals(_.list(1, 4, 9, 16, 25), result);
    }

    @Test
    public void staticMapWithNullInput() {
        assertEquals(Collections.emptyList(), _.map(null, square));
    }

    @Test
    public void chainedMapWithSquare() {
        Iterable<Integer> result = new _<Integer>(_.range(1, 6))
                .map(square)
                .value();

        assertEquals(Arrays.asList(new Integer[] {1, 4, 9, 16, 25}), result);
    }

    @Test
    public void staticMapMap() {
        Map<String, Integer> lengths = new HashMap<String, Integer>();
        lengths.put("foo", "foo".length());
        lengths.put("quux", "quux".length());

        Map<Long, String> expected = new HashMap<Long, String>();
        expected.put((long) "foo".length(), "foo");
        expected.put((long) "quux".length(), "quux");

        Map<Long, String> actual = _.map(lengths, new BiFunction<String, Integer, Map.Entry<Long, String>>() {
            @Override
            public Map.Entry<Long, String> apply(String s, Integer integer) {
                return new AbstractMap.SimpleEntry<Long, String>((long) integer, s);
            }
        });

        assertEquals(expected, actual);
    }

    // ----- _.filter ------------------------------------------------------------------------------

    @Test
    public void staticFilterWithIsEven() {
        List<Integer> result = _.filter(_.range(1, 6), isEven);

        assertEquals(Arrays.asList(new Integer[] {2, 4}), result);
    }

    @Test
    public void staticFilterWithNullInput() {
        assertEquals(Collections.emptyList(), _.filter(null, isEven));
    }

    @Test
    public void chainedFilterWithIsEven() {
        Iterable<Integer> result = new _<Integer>(_.range(1, 6))
                .filter(isEven)
                .value();

        assertEquals(_.list(2, 4), result);
    }

    // ----- _.find --------------------------------------------------------------------------------

    @Test
    public void staticFindWithIsEven() {
        assertEquals(
                2,
                (int) _.find(_.range(1, 6), isEven)
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
                (int) new _<Integer>(_.range(1, 6)).find(isEven)
        );
    }

    // ----- _.reduce ------------------------------------------------------------------------------

    @Test
    public void staticReduceWithSum() {
        Integer result = _.reduce(_.range(1, 6), sum, 0);

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
        Integer result = new _<Integer>(_.range(1, 6))
                .reduce(sum, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }

    @Test
    public void chainedComplexWithSumOfEvenSquares() {
        Integer sumOfEvenSquares = new _<Integer>(_.range(1, 11))
                .map(square)
                .filter(isEven)
                .reduce(sum, 0);

        assertTrue(4 + 16 + 36 + 64 + 100 == sumOfEvenSquares);
    }

    // ---- _.contains -----------------------------------------------------------------------------

    @Test
    public void staticContainsWithIntegers() {
        assertTrue(_.contains(_.range(1, 6), 4));
        assertFalse(_.contains(_.range(1, 4), 6));
    }

    @Test
    public void staticContainsWithNullInput() {
        assertFalse(_.contains(null, "foo"));
    }

    @Test
    public void chainedContainsWithIntegers() {
        assertTrue(new _<Integer>(_.range(1, 6)).contains(4));
        assertFalse(new _<Integer>(_.range(1, 4)).contains(6));
    }

    @Test
    public void staticContainsWithStringEquals() {
        assertTrue(_.contains(_.list("foo", "bar", "baz"), "foo", stringEquals));
        assertFalse(_.contains(_.list("foo", "bar"), "baz", stringEquals));
    }

    @Test
    public void chainedContainsWithStringEquals() {
        assertTrue(new _<String>(_.list("foo", "bar", "baz")).contains("foo", stringEquals));
        assertFalse(new _<String>(_.list("foo", "bar")).contains("baz", stringEquals));
    }

    // ----- _.sortBy ------------------------------------------------------------------------------

    @Test
    public void staticSortBy() {
        assertEquals(_.list("foo", "test", "qwert", "zuiopü"), _.sortBy(_.list("test", "zuiopü", "foo", "qwert"), length));
    }

    @Test
    public void staticSortByNullInput() {
        assertEquals(Collections.emptyList(), _.sortBy(null, length));
    }

    @Test
    public void chainedSortBy() {
        assertEquals(_.list("foo", "test", "qwert", "zuiopü"), new _<String>(_.list("test", "zuiopü", "foo", "qwert")).sortBy(length).value());
    }

    @Test
    public void chainedSortByNullInput() {
        assertEquals(Collections.emptyList(), new _<String>(null).sortBy(length).value());
    }

    // ----- _.groupBy -----------------------------------------------------------------------------

    @Test
    public void staticGroupBy() {
        Map<Integer, List<String>> expected = new HashMap<Integer, List<String>>();
        expected.put(3, _.list("foo", "bar", "baz"));
        expected.put(4, _.list("this", "shit"));

        assertEquals(expected, _.groupBy(_.list("foo", "bar", "baz", "this", "shit"), new Function<String, Integer>() {
            public Integer apply(String s) {
                return s.length();
            }
        }));
    }

    @Test
    public void staticGroupByNullInput() {
        assertEquals(Collections.emptyMap(), _.groupBy(null, _.identity(Integer.class)));
    }

    @Test
    public void chainedGroupBy() {
        Map<Integer, List<String>> expected = new HashMap<Integer, List<String>>();
        expected.put(3, _.list("foo", "bar", "baz"));
        expected.put(4, _.list("this", "shit"));

        assertEquals(expected, new _<String>(_.list("foo", "bar", "baz", "this", "shit"))
                .groupBy(new Function<String, Integer>() {
                    public Integer apply(String s) {
                        return s.length();
                    }
                })
        );
    }

    @Test
    public void chainedGroupNullInput() {
        assertEquals(Collections.emptyMap(), new _<Integer>(null).groupBy(_.identity(Integer.class)));
    }

    // ----- _.reject ------------------------------------------------------------------------------

    @Test
    public void staticRejectWithIsEven() {
        assertEquals(_.list(1,3,5), _.reject(_.range(1, 6), isEven));
    }

    @Test
    public void staticRejectWithIsEvenWithNullInput() {
        assertEquals(Collections.emptyList(), _.reject(null, isEven));
    }

    @Test
    public void chainedRejectWithIsEven() {
        assertEquals(_.list(1,3,5), new _<Integer>(_.range(1, 6)).reject(isEven).value());
    }

    @Test
    public void chainedRejectWithIsEvenWithNullInput() {
        assertEquals(Collections.emptyList(), new _<Integer>(null).reject(isEven).value());
    }

    // ----- _.every -------------------------------------------------------------------------------

    @Test
    public void staticEvery() {
        assertTrue(_.every(_.list(2,4,6), isEven));
        assertFalse(_.every(_.range(1, 6), isEven));
    }

    @Test
    public void staticEveryWithNullInput() {
        assertTrue(_.every(null, isEven));
    }

    @Test
    public void chainedEvery() {
        assertTrue(new _<Integer>(_.list(2,4,6)).every(isEven));
        assertFalse(new _<Integer>(_.range(1, 6)).every(isEven));
    }

    @Test
    public void chainedEveryWithNullInput() {
        assertTrue(new _<Integer>(null).every(isEven));
    }

    // ----- _.some --------------------------------------------------------------------------------

    @Test
    public void staticSome() {
        assertTrue(_.some(_.range(1, 6), isEven));
        assertFalse(_.some(_.list(1,3), isEven));
    }

    @Test
    public void staticSomeWithNullInput() {
        assertFalse(_.some(null, isEven));
    }

    @Test
    public void chainedSome() {
        assertTrue(new _<Integer>(_.range(1, 6)).some(isEven));
        assertFalse(new _<Integer>(_.list(1,3)).some(isEven));
    }

    @Test
    public void chainedSomeWithNullInput() {
        assertFalse(new _<Integer>(null).some(isEven));
    }

    // ----- _.size --------------------------------------------------------------------------------

    @Test
    public void staticSize() {
        assertEquals(3, _.size(_.range(1, 4)));
    }

    @Test
    public void staticSizeWithNullInput() {
        assertEquals(0, _.size(null));
    }

    @Test
    public void chainedSize() {
        assertEquals(3, new _<Integer>(_.range(1, 4)).size());
    }

    // ----- _.first -------------------------------------------------------------------------------

    @Test
    public void staticFirst() {
        assertEquals(_.range(1, 4), _.first(_.range(1, 5), 3));
    }

    @Test
    public void staticFirstWithNullInput() {
        assertEquals(Collections.emptyList(), _.first(null, 3));
    }

    @Test
    public void staticFirstDefaultN() {
        assertTrue(1 == _.first(_.range(1, 5)));
    }

    @Test
    public void chainedFirst() {
        assertEquals(_.range(1, 4),new _<Integer>(_.range(1, 5)).first(3).value());
    }

    @Test
    public void chainedFirstDefaultN() {
        assertTrue(1 == new _<Integer>(_.range(1, 5)).first());
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
        assertEquals(_.list("foo"), new _<String>(_.list("foo", "bar", "baz")).initial(2).value());
    }

    @Test
    public void chainedInitialDefaultN() {
        assertEquals(_.list("foo", "bar"), new _<String>(_.list("foo", "bar", "baz")).initial().value());
    }

    // ----- _.last --------------------------------------------------------------------------------

    @Test
    public void staticLast() {
        assertEquals(_.range(4, 6), _.last(_.range(1, 6), 2));
    }

    @Test
    public void staticLastDefaultN() {
        assertTrue(4 == _.last(_.range(1, 5)));
    }

    @Test
    public void chainedLast() {
        assertEquals(_.range(4, 6), new _<Integer>(_.range(1, 6)).last(2).value());
    }

    @Test
    public void chainedLastDefaultN() {
        assertTrue(4 == new _<Integer>(_.range(1, 5)).last());
    }

    // ----- _.rest --------------------------------------------------------------------------------

    @Test
    public void staticRest() {
        assertEquals(_.range(3, 5), _.rest(_.range(1, 5), 2));
    }

    @Test
    public void staticRestWithNullInput() {
        assertEquals(Collections.emptyList(), _.rest(null, 2));
    }

    @Test
    public void staticRestDefaultN() {
        assertEquals(_.range(2, 5), _.rest(_.range(1, 5)));
    }

    @Test
    public void chainedRest() {
        assertEquals(_.range(3, 5), new _<Integer>(_.range(1, 5)).rest(2).value());
    }

    @Test
    public void chainedRestDefaultN() {
        assertEquals(_.range(2, 5), new _<Integer>(_.range(1, 5)).rest().value());
    }

    // ----- _.zip ---------------------------------------------------------------------------------

    @Test
    public void staticZipSameLengths() {
        assertEquals(_.list(_.entry(0,3), _.entry(1,4), _.entry(2,5)), _.zip(_.range(3), _.range(3,6)));
    }

    @Test
    public void staticZipFirstIsLonger() {
        assertEquals(_.list(_.entry(0,3), _.entry(1,4)), _.zip(_.range(3), _.range(3,5)));
    }

    @Test
    public void staticZipSecondIsLonger() {
        assertEquals(_.list(_.entry(0,3), _.entry(1,4), _.entry(2,5)), _.zip(_.range(3), _.range(3,9)));
    }

    @Test
    public void staticZipNullInput() {
        assertEquals(Collections.emptyList(), _.zip(null, _.range(4)));
        assertEquals(Collections.emptyList(), _.zip(_.range(4), null));
    }

    @Test
    public void chainedZipSameLengths() {
        assertEquals(_.list(_.entry(0,4), _.entry(1,5), _.entry(2,6), _.entry(3,7)),
                new _<Integer>(_.range(4)).zip(_.range(4,8)).value());
    }

    @Test
    public void chainedZipFirstIsLonger() {
        assertEquals(_.list(_.entry(0,3), _.entry(1,4)),
                new _<Integer>(_.range(4)).zip(_.range(3,5)).value());
    }

    @Test
    public void chainedZipSecondIsLonger() {
        assertEquals(_.list(_.entry(0,3), _.entry(1,4)),
                new _<Integer>(_.range(2)).zip(_.range(3,9)).value());
    }

    @Test
    public void chainedZipNullInput() {
        assertEquals(Collections.emptyList(), new _<Integer>(_.range(10)).zip(null).value());
    }

    // ----- _.range -------------------------------------------------------------------------------

    @Test
    public void rangeOptimisticInputs() {
        assertEquals(_.list(1,2,3,4,5), _.range(1, 6, 1));
    }

    @Test
    public void rangeNegativeStep() {
        assertEquals(_.list(0,-1,-2,-3,-4), _.range(0, -5, -1));
    }

    @Test
    public void rangeOddStep() {
        assertEquals(_.list(0, 2, 4), _.range(0, 5, 2));
        assertEquals(_.list(0, -2, -4), _.range(0, -5, -2));
    }

    @Test
    public void rangeSameStartStop() {
        List<Integer> empty = Collections.emptyList();
        assertEquals(empty, _.range(0, 0, 1));
    }

    @Test
    public void rangeZeroStep() {
        List<Integer> empty = Collections.emptyList();
        assertEquals(empty, _.range(0, 100, 0));
    }

    @Test
    public void rangeStartGreaterStopPositiveStep() {
        List<Integer> empty = Collections.emptyList();
        assertEquals(empty, _.range(5, 0, 1));
    }

    @Test
    public void rangeDefaultStep() {
        assertEquals(_.list(0,1,2,3,4), _.range(0, 5));
    }

    @Test
    public void rangeDefaultStepStartGreaterStop() {
        List<Integer> empty = Collections.emptyList();
        assertEquals(empty, _.range(0, -5));
    }

    @Test
    public void rangeDefaultStart() {
        assertEquals(_.list(0,1,2,3,4), _.range(5));
    }

    // ----- _.wrap --------------------------------------------------------------------------------

    @Test
    public void wrap() {
        final String before = "before ++ ";
        final String after = " ++ after";
        final Function<String, String> expected = new Function<String, String>() {
            @Override
            public String apply(String s) {
                return before + s + after;
            }
        };
        final Function<String, String> actual = _.wrap(_.identity(String.class), new Function<Function<String, String>, Function<String, String>>() {
            public Function<String, String> apply(final Function<String, String> wrapped) {
                return new Function<String, String>() {
                    public String apply(String s){
                        return before + wrapped.apply(s) + after;
                    }
                };
            }
        });

        _.each(_.list("foo", "bar", "baz"), new Consumer<String>() {
            @Override
            public void accept(String s) {
                assertEquals(expected.apply(s), actual.apply(s));
            }
        });
    }

    // ----- _.negate ------------------------------------------------------------------------------

    @Test
    public void negatePredicate() {
        final Predicate<String> ofEvenLength = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.length() % 2 == 0;
            }
        };
        final Predicate<String> ofOddLength = _.negate(ofEvenLength);
        _.each(_.list("foo", "lorem", "quux"), new Consumer<String>() {
            @Override
            public void accept(String s) {
                assertTrue(ofEvenLength.test(s) ^ ofOddLength.test(s));
            }
        });
    }

    @Test
    public void negateBiPredicate() {
        final BiPredicate<String, Integer> isOfLength = new BiPredicate<String, Integer>() {
            @Override
            public boolean test(String s, Integer integer) {
                return s.length() == integer;
            }
        };
        final BiPredicate<String, Integer> isNotOfLength = _.negate(isOfLength);
        _.each(_.list("foo:3", "lorem:2", "quux:12"), new Consumer<String>() {
            @Override
            public void accept(String s) {
                String[] unzip = s.split(":");
                assertTrue(isOfLength.test(unzip[0], Integer.parseInt(unzip[1])) ^ isNotOfLength.test(unzip[0], Integer.parseInt(unzip[1])));
            }
        });
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
        assertEquals(Arrays.asList(new String[] {"foo", "bar"}), _.list(new String[] {"foo", "bar"}));
    }

    @Test
    public void listWithNullInput() {
        assertEquals(Collections.emptyList(), _.list());
    }

    // ----- _.dictionary --------------------------------------------------------------------------

    @Test
    public void dictionary() {
        Map<String, Integer> expected = new HashMap<String, Integer>();
        expected.put("foo", "foo".length());
        expected.put("bar", "bar".length());

        Map<String, Integer> actual = _.dictionary(
                _.entry("foo", "foo".length()),
                _.entry("bar", "bar".length())
        );

        assertEquals(expected, actual);
    }

    @Test
    public void dictionaryWithNullInput() {
        assertEquals(Collections.emptyMap(), _.dictionary(null));
    }

    // ----- _.entry -------------------------------------------------------------------------------

    @Test
    public void entry() {
        assertEquals(new AbstractMap.SimpleEntry<String,Integer>("foo", 3), _.entry("foo", 3));
    }

    // ----- _.join --------------------------------------------------------------------------------

    @Test
    public void staticJoin() {
        assertEquals("foo::bar", _.join(_.list("foo", "bar"), "::"));
    }

    @Test
    public void staticJoinDefaultSeparator() {
        assertEquals("foo,bar", _.join(_.list("foo", "bar")));
    }

    @Test
    public void staticJoinWithNullInput() {
        assertEquals("", _.join(null, ","));
    }

    @Test
    public void staticJoinDefaultSeparatorWithNullInput() {
        // need to cast -- otherwise multiple methods match _.join(null)
        assertEquals("", _.join((Iterable<String>) null));
    }

    @Test
    public void chainedJoin() {
        assertEquals("foo::bar", new _<String>(_.list("foo", "bar")).join("::"));
    }

    @Test
    public void chainedJoinDefaultSeparator() {
        assertEquals("foo,bar", new _<String>(_.list("foo", "bar")).join());
    }

    @Test
    public void chainedJoinWithNullInput() {
        assertEquals("", new _<String>(null).join("::"));
    }

    @Test
    public void chainedJoinDefaultSeparatorWithNullInput() {
        assertEquals("", new _<String>(null).join());
    }

    // ----- _.stringify ---------------------------------------------------------------------------

    @Test
    public void staticStringifyTerminalCases() {
        assertEquals("1", _.stringify(1));
        assertEquals("1.0", _.stringify(1.0));
        assertEquals("true", _.stringify(true));
        assertEquals("\"foo\"", _.stringify("foo"));
    }

    @Test
    public void staticStringifyListCase() {
        assertEquals("[0,1,2,3]", _.stringify(_.range(4)));
    }

    @Test
    public void staticStringifyMapCase() {
        Map<String, Integer> json = new HashMap<String, Integer>();
        json.put("0", 1);
        json.put("1", 5);
        json.put("2", 9);
        assertEquals("{\"0\":1,\"1\":5,\"2\":9}", _.stringify(json));
    }

    @Test
    public void staticStringifyComplexCase() {
        Map<String, Integer> frequencies = new HashMap<String, Integer>();
        frequencies.put("a", 20);
        frequencies.put("b", 4);
        frequencies.put("c", 12);

        Map<String, String> complex = new HashMap<String, String>();
        complex.put("name", _.stringify(_.list("foo", "bar")));
        complex.put("frequencies", _.stringify(frequencies));

        assertEquals("{\"name\":[\"foo\",\"bar\"],\"frequencies\":{\"a\":20,\"b\":4,\"c\":12}}", _.stringify(complex));
    }

    @Test
    public void staticStringifyComplexListCase() {
        Map<String, String> json1 = new HashMap<String, String>();
        json1.put("1foo", "[\"bar\"]");
        json1.put("2goo", "[\"gl\"]");
        Map<String, String> json2 = new HashMap<String, String>();
        json2.put("3oo", "[\"ps\"]");
        json2.put("4no", "[\"oo\"]");

        assertEquals("[{\"1foo\":[\"bar\"],\"2goo\":[\"gl\"]},{\"3oo\":[\"ps\"],\"4no\":[\"oo\"]}]", _.stringify(_.list(json1, json2)));
    }

    @Test
    public void staticStringifyComplexMixedCase() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("string", "thisIsAString");
        json.put("number", 1337);
        json.put("list", _.list(1,2,3,4,5));
        Map<String, Object> nested = new HashMap<String, Object>();
        nested.put("string", "thisIsAnotherString");
        nested.put("number", 12345);
        json.put("map", nested);

        assertEquals("{\"number\":1337,\"string\":\"thisIsAString\",\"list\":[1,2,3,4,5],\"map\":{\"number\":12345,\"string\":\"thisIsAnotherString\"}}", _.stringify(json));
    }

    // ----- _.identity ----------------------------------------------------------------------------

    @Test
    public void identity() {
        final Function<String, String> expected = new Function<String, String>() {
            public String apply(String s) {
                return s;
            }
        };
        final Function<String, String> actual = _.identity(String.class);

        _.each(_.list("foo", "bar", "baz"), new Consumer<String>() {
            @Override
            public void accept(String s) {
                assertEquals(expected.apply(s), actual.apply(s));
            }
        });
    }

    // ----- _.constant ----------------------------------------------------------------------------

    @Test
    public void constant() {
        assertEquals("foo", _.constant("foo").get());
    }
}
