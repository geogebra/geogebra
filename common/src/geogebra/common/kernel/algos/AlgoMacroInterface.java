package geogebra.common.kernel.algos;

import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.geos.GeoList;

public interface AlgoMacroInterface {
	public void initFunction(FunctionNVar f);

	public void initList(GeoList l, GeoList geoList);
}
