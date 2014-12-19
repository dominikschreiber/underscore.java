package com.dominikschreiber.underscore;

public interface BinaryFn<InA, InB, Out> {
    public Out apply(InA a, InB b);
}
