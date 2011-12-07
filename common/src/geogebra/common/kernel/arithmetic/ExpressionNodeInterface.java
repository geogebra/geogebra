package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.AbstractKernel;

public interface ExpressionNodeInterface extends ExpressionValue {
	public ExpressionNodeInterface getCopy(AbstractKernel kernel);
}
