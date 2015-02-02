package java.util.function;

/**
 * <p>Represents an operation that accepts a single input argument and returns no result.
 * Unlike most other functional interfaces, {@code Consumer} is expected to operate
 * via side-effects.</p>
 * <p>This is a functional interface whose functional method is {@link #accept(Object)}</p>
 * @param <T> the type of the input to the operation
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html">Java 8 Consumer</a>
 */
public interface Consumer<T> {
    /**
     * <p>Performs this operation on the given argument.</p>
     * @param t the input argument
     */
    public void accept(T t);

    // not implementable pre Java 8:

    // public default Consumer<T> andThen(Consumer<? super T> after);
}
