package geogebra.common.kernel.parser;

import geogebra.common.kernel.arithmetic.ExpressionNode;

public interface ParserInterface {
	public ExpressionNode parseExpression(String parseString)
			throws Exception;
}
