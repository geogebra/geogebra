package geogebra.adapters;

public class MaxIterationsExceededException extends geogebra.common.adapters.MaxIterationsExceededException {

	private org.apache.commons.math.MaxIterationsExceededException miee;

	public MaxIterationsExceededException(org.apache.commons.math.MaxIterationsExceededException m) {
		miee = m;
	}

	org.apache.commons.math.MaxIterationsExceededException getImpl() {
		return miee;
	}
}
