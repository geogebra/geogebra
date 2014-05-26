package geogebra.common.kernel;

import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.roots.RealRootFunction;

public interface DistanceFunction extends RealRootFunction {

	double evaluate(double pathParam);

	void setDistantPoint(GeoPointND p);

}
