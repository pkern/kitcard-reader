package de.Ox539.kitcard.reader;

/*
 * Pair: a simple Pair implementation
 * Roughly based on javatuples' implementation, which is licensed under
 * the Apache License, Version 2.0.
 */

public final class Pair<A,B> {
	private final A val0;
	private final B val1;

	public Pair(final A value0, final B value1) {
		this.val0 = value0;
		this.val1 = value1;
	}

    public A getValue0() {
        return this.val0;
    }


    public B getValue1() {
        return this.val1;
    }
}
