package java.util.function;

/**
 * <p>Represents an operation that accepts two input arguments and returns no results.
 * This is the two-arity specialization of {@link Consumer}. Unlike most other functional
 * interfaces, {@code BiConsumer} is expected to operate via side-effects.</p>
 * <p>This is a functional interface whose functional method is {@link #accept(Object,Object)}.</p>
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/BiConsumer.html">Java 8 BiConsumer</a>
 */
public interface BiConsumer<T, U> {
    /**
     * <p>Performs this operation on the given arguments.</p>
     * @param t the first input argument
     * @param u the second input argument
     */
    public void accept(T t, U u);

    // not implementable pre Java 8:

    // public default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after)
}
