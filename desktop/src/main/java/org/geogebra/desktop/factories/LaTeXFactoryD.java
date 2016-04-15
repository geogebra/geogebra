package org.geogebra.desktop.factories;

import org.geogebra.common.factories.LaTeXFactory;
import org.geogebra.common.util.LaTeXCache;
import org.geogebra.desktop.util.GeoLaTeXCache;

public class LaTeXFactoryD extends LaTeXFactory {
	@Override
	public LaTeXCache newLaTeXCache() {
		return new GeoLaTeXCache();
	}
}
