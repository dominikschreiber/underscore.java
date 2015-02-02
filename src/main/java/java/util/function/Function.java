package java.util.function;

/**
 * <p>Represents a function that accepts one argument and produces a result.</p>
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html">Java 8 Function</a>
 */
public interface Function<T, R> {
    /**
     * <p>the actual callback method for functional-style programming</p>
     * @param input
     * @return the result of the computation (or {@link Void} if you do not want to return anything)
     */
    public R apply(T input);

    // not implementable pre Java 8:
    
    // public default <V> Function<T, V> andThen(Function<? super R, ? extends V> after);
    // public default <V> Function<T, V> compose(Function<? super V, ? extends T> before);
    // public static <T> Function<T, T> identity();
}