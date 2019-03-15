package org.geogebra.common.spy.builder;

public abstract class SpyBuilder<T> {

	private T spy;

	public T getSpy() {
		if (spy == null) {
			spy = createSpy();
		}
		return spy;
	}

	abstract T createSpy();
}
