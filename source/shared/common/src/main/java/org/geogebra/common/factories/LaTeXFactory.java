package org.geogebra.common.factories;

import org.geogebra.common.util.LaTeXCache;

/**
 * Factory for LaTeX related objects
 */
public abstract class LaTeXFactory {
	/** platform dependent prototype */
	private static volatile LaTeXFactory prototype;

	private static final Object lock = new Object();

	public static LaTeXFactory getPrototype() {
		return prototype;
	}

	/**
	 * @param p
	 *            prototype
	 */
	public static void setPrototypeIfNull(LaTeXFactory p) {

		synchronized (lock) {
			if (prototype == null) {
				prototype = p;
			}
		}
	}

	/**
	 * @return LaTeX cache
	 */
	public abstract LaTeXCache newLaTeXCache();

}
