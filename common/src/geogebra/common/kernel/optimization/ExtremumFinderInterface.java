package geogebra.common.kernel.optimization;

import geogebra.common.kernel.roots.RealRootFunction;


public interface ExtremumFinderInterface {

	double findMinimum(double left, double right,
			RealRootFunction distFun, double minPrecision);

	
}
