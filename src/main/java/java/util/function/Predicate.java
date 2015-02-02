package java.util.function;

/**
 * <p>Represents a predicate (boolean-valued function) of one argument.</p>
 * <p>This is a functional interface whose functional method is {@link #test(Object)}.</p>
 * @param <T> the type of the input to the predicate
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html">Java 8 Predicate</a>
 */
public interface Predicate<T> {
    /**
     * <p>Evaluates this predicate on the given argument.</p>
     * @param t the input argument
     * @return true if the input argument matches the predicate, otherwise false
     */
    public boolean test(T t);

    // not implementable pre Java 8:

    // public default Predicate<T> and(Predicate<? super T> other);
    // public static <T> Predicate<T> isEqual(Object targetRef);
    // public default Predicate<T> negate();
    // public default Predicate<T> or(Predicate<? super T> other);
}
