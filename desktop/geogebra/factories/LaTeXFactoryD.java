package geogebra.factories;

import geogebra.common.util.LaTeXCache;
import geogebra.util.GeoLaTeXCache;

public class LaTeXFactoryD extends geogebra.common.factories.LaTeXFactory {
	@Override
	public LaTeXCache newLaTeXCache() {
		return new GeoLaTeXCache();
	}
}
