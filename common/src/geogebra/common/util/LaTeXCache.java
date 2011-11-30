package geogebra.common.util;

import geogebra.common.awt.ColorAdapter;

public interface LaTeXCache {
	public void remove();
	public Object getCachedLaTeXKey(String a,int b,int c,ColorAdapter m);
}
