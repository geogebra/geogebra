package geogebra.common.factories;

import geogebra.common.adapters.Complex;
import geogebra.common.adapters.LegendreGaussIntegrator;
import geogebra.common.adapters.RealRootAdapter;//Apache: UnivariateRealFunction
import geogebra.common.adapters.RealMatrix;
import geogebra.common.kernel.roots.RealRootFunction;

public abstract class AdapterFactory {
	public static AdapterFactory prototype = null;

	public abstract Complex newComplex(double r, double i);
	public abstract RealRootAdapter newRealRootAdapter(RealRootFunction f);
	public abstract LegendreGaussIntegrator newLegendreGaussIntegrator(int num, int max_iter);
	public abstract RealMatrix newRealMatrixImpl(int rows, int cols);
}
