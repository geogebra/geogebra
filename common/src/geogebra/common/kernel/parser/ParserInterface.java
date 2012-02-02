package geogebra.common.kernel.parser;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.Function;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.ValidExpression;

public interface ParserInterface {
	public ExpressionNode parseExpression(String parseString) throws Exception;

	public Function parseFunction(String string) throws Exception;

	public FunctionNVar parseFunctionNVar(String string)
			throws Exception;

	public ValidExpression parseGeoGebraExpression(String str) throws Exception;

	public String parseLabel(String label) throws Exception;
}
