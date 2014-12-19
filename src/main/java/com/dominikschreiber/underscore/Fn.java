package com.dominikschreiber.underscore;

/**
 * <p>Allows to implement anonymous callback functionality known from
 * more functional programming languages</p>
 * <p>Use it i.e. to create a custom logging output on the fly:</p>
 * <pre>
 * OutstreamHttpClient client = getNewOutstreamHttpClient();
 * client.setLog(new Fn<String, Void>() {
 *     public Void apply(String message) {
 *         Log.d("MyClient", message);
 *         return null;
 *     }
 * });
 * </pre>
 * @param <In> type of the input parameter (i.e. {@link String})
 * @param <Out> type of the result of the computation (i.e. {@link String} or {@link Void})
 */
public interface Fn<In, Out> {

    /**
     * <p>the actual callback method for functional-style programming</p>
     * @param input
     * @return the result of the computation (or {@link Void} if you do not want to return anything)
     */
    public Out apply(In input);
}