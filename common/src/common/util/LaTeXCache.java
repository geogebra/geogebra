package geogebra.common.util;

import geogebra.common.awt.Color;

public interface LaTeXCache {
	public void remove();
	public Object getCachedLaTeXKey(String a,int b,int c,Color m);
}
