package org.geogebra.common.util;

public interface LaTeXCache {
	public void remove();

	public Object getCachedLaTeXKey(String a, int b, int c, Object color);
}
