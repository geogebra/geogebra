package geogebra.factories;

import geogebra.adapters.Complex;
import geogebra.kernel.roots.RealRootAdapter;
import geogebra.common.kernel.roots.RealRootFunction;

public class AdapterFactory extends geogebra.common.factories.AdapterFactory {

	public AdapterFactory() { }

	public geogebra.common.adapters.Complex newComplex(double r, double i) {
		return new Complex(r, i);
	}

	public geogebra.common.adapters.RealRootAdapter newRealRootAdapter(RealRootFunction f) {
		return new RealRootAdapter(f);
	}
}
