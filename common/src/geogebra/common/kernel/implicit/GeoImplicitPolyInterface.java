package geogebra.common.kernel.implicit;

import geogebra.common.kernel.arithmetic.ValidExpression;

public interface GeoImplicitPolyInterface {

	double evalPolyAt(double evaluate, double evaluate2);

	public void setCoeff(double[][] c);

	void setUserInput(ValidExpression equ);
}
