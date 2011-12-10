package geogebra.common.kernel.parser;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionInterface;
import geogebra.common.kernel.arithmetic.FunctionNVarInterface;
import geogebra.common.kernel.arithmetic.ValidExpression;

public interface ParserInterface {
	public ExpressionNode parseExpression(String parseString)
			throws Exception;

	public FunctionInterface parseFunction(String string)
			throws Exception;;
	public FunctionNVarInterface parseFunctionNVar(String string)
			throws Exception;

	public ValidExpression parseGeoGebraExpression(String str) throws Exception;;

	public String parseLabel(String label) throws Exception;
}
