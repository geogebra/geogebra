package org.geogebra.common.factories;

import org.geogebra.common.util.LaTeXCache;

/**
 * Factory for LaTeX related objects
 */
public abstract class LaTeXFactory {
	/** platform dependent prototype */
	public static LaTeXFactory prototype;

	/**
	 * @return LaTeX cache
	 */
	public abstract LaTeXCache newLaTeXCache();

}
