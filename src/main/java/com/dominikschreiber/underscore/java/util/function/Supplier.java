package com.dominikschreiber.underscore.java.util.function;

/**
 * <p>Represents a supplier of results.</p>
 * <p>There is no requirement that a new or distinct result be returned each time the supplier is invoked.</p>
 * <p>This is a functional interface whose functional method is {@link #get()}</p>
 * @param <T> the type of results supplied by this supplier
 * @see <a href="http://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html">Java 8 Supplier</a>
 */
public interface Supplier<T> {
    /**
     * <p>Gets a result</p>
     * @return a result
     */
    public T get();
}
