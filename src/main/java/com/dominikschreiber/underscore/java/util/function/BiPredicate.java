package com.dominikschreiber.underscore.java.util.function;

/**
 * <p>Represents a predicate (boolean-valued function) of two arguments. This is the two-arity
 * specialization of {@link Predicate}</p>
 * <p>This is a functional interface whose functional method is {@link #test(Object,Object)}.</p>
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument to the predicate
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/BiPredicate.html">Java 8 BiPredicate</a>
 */
public interface BiPredicate<T, U> {
    /**
     * <p>Evaluates this predicate on the given arguments.</p>
     * @param t the first input argument
     * @param u the second input argument
     * @return {@code true} if the arguments match the predicate, otherwise {@code false}
     */
    public boolean test(T t, U u);

    // not implementable pre Java 8:

    // public default BiPredicate<T,U> and(BiPredicate<? super T,? super U> other);
    // public default BiPredicate<T,U> negate();
    // public default BiPredicate<T,U> or(BiPredicate<? superT,? super U> other);
}
