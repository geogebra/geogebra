package org.geogebra.common.util;

import org.geogebra.common.awt.GColor;

public interface LaTeXCache {
	public void remove();

	public Object getCachedLaTeXKey(String a, int b, int c, GColor m);
}
