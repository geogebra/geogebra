package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;

public class VectorToMatrix {
	private final Kernel kernel;

	/**
	 * @param kernel the kernel.
	 */
	public VectorToMatrix(Kernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * Builds a matrix-like string {{x}, {y}, {z}} from ExpressionNode
	 *
	 * @param template StringTemplate to use.
	 * @param expressionNode as input.
	 * @return the matrix-like string.
	 */
	public String build(StringTemplate template, ExpressionNode expressionNode) {

		MyVecNDNode vecNode = (MyVecNDNode) expressionNode.unwrap();

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(surroundWithBrackets(vecNode.getX(), template));
		sb.append(", ");
		sb.append(surroundWithBrackets(vecNode.getY(), template));
		ExpressionValue z = vecNode.getZ();
		if (z != null) {
			sb.append(", ");
			sb.append(surroundWithBrackets(z, template));
		}
		sb.append("}");
		return sb.toString();

	}

	/**
	 * Builds a matrix-like string {{x}, {y}..} from x, y...
	 * @param tpl the StringTemplate to use.
	 * @param coordinates the values of the vector build from.
	 * @return {{x}, {y}} format string.
	 */
	public String build(StringTemplate tpl, double... coordinates) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String separator = "";
		for (double i: coordinates) {
			sb.append(separator);
			sb.append(surroundWithBrackets(i, tpl));
			separator = ", ";
		}
		sb.append("}");
		return sb.toString();
	}

	private String surroundWithBrackets(ExpressionValue value, StringTemplate tpl) {
		return "{" + value.toLaTeXString(true, tpl) + "}";
	}

	private String surroundWithBrackets(double value, StringTemplate tpl) {
		return "{" + kernel.format(value, tpl) + "}";
	}

}