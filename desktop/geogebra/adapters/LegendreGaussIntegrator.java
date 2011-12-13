package geogebra.adapters;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;

import geogebra.common.adapters.RealRootAdapter;

public class LegendreGaussIntegrator implements geogebra.common.adapters.LegendreGaussIntegrator {

	private org.apache.commons.math.analysis.integration.LegendreGaussIntegrator lgi;

	public LegendreGaussIntegrator(int num, int max_iter) throws IllegalArgumentException {
		lgi = new org.apache.commons.math.analysis.integration.LegendreGaussIntegrator(num, max_iter);
	}

	public LegendreGaussIntegrator(org.apache.commons.math.analysis.integration.LegendreGaussIntegrator l) {
		lgi = l;
	}

	org.apache.commons.math.analysis.integration.LegendreGaussIntegrator getImpl() {
		return lgi;
	}

	public double integrate(RealRootAdapter rr, double xfrom, double xto)
		throws IllegalArgumentException, ConvergenceException, FunctionEvaluationException {
		return lgi.integrate((geogebra.kernel.roots.RealRootAdapter)rr, xfrom, xto);
	}
}
