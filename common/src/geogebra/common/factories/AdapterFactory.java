package geogebra.common.factories;

import geogebra.common.adapters.Complex;
import geogebra.common.adapters.RealRootAdapter;//Apache: UnivariateRealFunction
import geogebra.common.kernel.roots.RealRootFunction;

public abstract class AdapterFactory {
	public static AdapterFactory prototype = null;

	public abstract Complex newComplex(double r, double i);
	public abstract RealRootAdapter newRealRootAdapter(RealRootFunction f);
}
