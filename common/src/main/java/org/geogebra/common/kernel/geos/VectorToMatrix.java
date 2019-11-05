package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;

public class VectorToMatrix {
	private final Kernel kernel;

	public VectorToMatrix(Kernel kernel) {
		this.kernel = kernel;
	}

	public String build(StringTemplate template, ExpressionNode expressionNode) {
		MyVecNDNode vecNode = (MyVecNDNode)(expressionNode.getLeft());

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

	private String surroundWithBrackets(ExpressionValue value, StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(value.toLaTeXString(true, tpl));
		sb.append("}");
		return sb.toString();
	}

}
