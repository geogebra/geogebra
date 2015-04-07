package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public interface AlgoMacroInterface {
	public void initFunction(FunctionNVar f);

	public void initList(GeoList l, GeoList geoList);

	public boolean drawBefore(GeoElement geoElement, GeoElement other);
}
