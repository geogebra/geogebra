package org.geogebra.common.spy.builder;

/**
 * Builds a spy.
 * @param <T> The type of the spy.
 */
public abstract class SpyBuilder<T> {

	private T spy;

	/**
	 * Creates the spy instance if it's not already created and returns it.
	 * @return The spy instance.
	 */
	public T getSpy() {
		if (spy == null) {
			spy = createSpy();
		}
		return spy;
	}

	/**
	 * Creates the spy.
	 * @return The created spy.
	 */
	abstract T createSpy();
}
