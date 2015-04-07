package org.geogebra.common.factories;

import org.geogebra.common.util.LaTeXCache;

public abstract class LaTeXFactory {
	public static LaTeXFactory prototype;

	public abstract LaTeXCache newLaTeXCache();

}
