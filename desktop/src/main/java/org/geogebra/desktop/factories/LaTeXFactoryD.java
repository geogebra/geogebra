package org.geogebra.desktop.factories;

import org.geogebra.common.util.LaTeXCache;
import org.geogebra.desktop.util.GeoLaTeXCache;

public class LaTeXFactoryD extends org.geogebra.common.factories.LaTeXFactory {
	@Override
	public LaTeXCache newLaTeXCache() {
		return new GeoLaTeXCache();
	}
}
