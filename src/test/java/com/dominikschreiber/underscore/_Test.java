package com.dominikschreiber.underscore;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class _Test {
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
    public void staticMapWithSquare() {
        List<Integer> result = _.map(range(6), new Fn<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input * input;
            }
        });

        assertEquals(Arrays.asList(new Integer[] {1, 4, 9, 16, 25}), result);
    }

    @Test
    public void staticFilterWithIsEven() {
        List<Integer> result = _.filter(range(6), new Fn<Integer, Boolean>() {
           public Boolean apply(Integer input) {
               return input % 2 == 0;
           }
        });

        assertEquals(Arrays.asList(new Integer[] {2, 4}), result);
    }

    @Test
    public void staticReduceWithSum() {
        Integer result = _.reduce(range(6), new BinaryFn<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer prev, Integer now) {
                return prev + now;
            }
        }, 0);

        assertTrue(1 + 2 + 3 + 4 + 5 == result);
    }
}
