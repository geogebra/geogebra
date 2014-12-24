package geogebra.common.factories;

import geogebra.common.util.LaTeXCache;

public abstract class LaTeXFactory {
	public static LaTeXFactory prototype;

	public abstract LaTeXCache newLaTeXCache();

}
