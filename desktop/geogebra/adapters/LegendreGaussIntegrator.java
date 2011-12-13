package geogebra.adapters;

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
		throws IllegalArgumentException, geogebra.common.adapters.ConvergenceException, geogebra.common.adapters.FunctionEvaluationException, geogebra.common.adapters.MaxIterationsExceededException {
		try {
			return lgi.integrate((geogebra.kernel.roots.RealRootAdapter)rr, xfrom, xto);
		} catch (org.apache.commons.math.MaxIterationsExceededException e) {
			throw new geogebra.adapters.MaxIterationsExceededException(e);
		} catch (org.apache.commons.math.ConvergenceException e) {
			throw new geogebra.adapters.ConvergenceException(e);
		} catch (org.apache.commons.math.FunctionEvaluationException e) {
			throw new geogebra.adapters.FunctionEvaluationException(e);
		} catch (IllegalArgumentException e) {
			throw e;
		}
	}
}
