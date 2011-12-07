package geogebra.common.kernel.parser;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.FunctionInterface;
import geogebra.common.kernel.arithmetic.FunctionNVarInterface;

public interface ParserInterface {
	public ExpressionNode parseExpression(String parseString)
			throws Exception;

	public FunctionInterface parseFunction(String string)
			throws Exception;;
	public FunctionNVarInterface parseFunctionNVar(String string)
			throws Exception;;
}
