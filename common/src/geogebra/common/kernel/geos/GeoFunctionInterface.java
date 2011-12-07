package geogebra.common.kernel.geos;

import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionInterface;

public interface GeoFunctionInterface extends ExpressionValue,GeoElementInterface{
	public FunctionInterface getFunction();

	public void setDefined(boolean b);

	public void setFunction(FunctionInterface deriv);
}
