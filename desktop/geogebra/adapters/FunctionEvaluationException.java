package geogebra.adapters;

public class FunctionEvaluationException extends geogebra.common.adapters.FunctionEvaluationException {

	private org.apache.commons.math.FunctionEvaluationException fee;

	public FunctionEvaluationException(org.apache.commons.math.FunctionEvaluationException m) {
		fee = m;
	}

	org.apache.commons.math.FunctionEvaluationException getImpl() {
		return fee;
	}
}
