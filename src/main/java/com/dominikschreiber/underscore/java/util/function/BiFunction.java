package com.dominikschreiber.underscore.java.util.function;

/**
 * <p>Represents a function that accepts two arguments and produces a result.
 * This is the two-arity specialization of {@link Function}.</p>
 * <p>This is a functional interface whose functional method is {@link #apply(Object, Object)}.</p>
 * @param <T> the type of the first argument of the function
 * @param <U> the type of the second argument of the function
 * @param <R> the type of the result of the function
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/BiFunction.html">Java 8 BiFunction</a>
 */
public interface BiFunction<T, U, R> {
    /**
     * <p>Applies this function to the given arguments.</p>
     * @param t the first argument
     * @param u the second argument
     * @return the function result
     */
    public R apply(T t, U u);

    // not implementable pre Java 8:

    // public default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after);
}
