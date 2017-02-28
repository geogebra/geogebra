package org.geogebra.common.kernel.roots;

import org.apache.commons.math3.analysis.UnivariateFunction;

// Interface for RealRoot class
// returns value of function whose root is required
public interface RealRootFunction extends UnivariateFunction {
	double evaluate(double x);
}
