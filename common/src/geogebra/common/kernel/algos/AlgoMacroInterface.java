package geogebra.common.kernel.algos;

import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.geos.GeoListInterface;

public interface AlgoMacroInterface {
	public void initFunction(FunctionNVar f);

	public void initList(GeoListInterface l, GeoListInterface geoList);
}
