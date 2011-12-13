package geogebra.adapters;

public class ConvergenceException extends geogebra.common.adapters.ConvergenceException {

	private org.apache.commons.math.ConvergenceException ce;

	public ConvergenceException(org.apache.commons.math.ConvergenceException m) {
		ce = m;
	}

	org.apache.commons.math.ConvergenceException getImpl() {
		return ce;
	}
}
