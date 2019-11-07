package org.geogebra.common.kernel.geos;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoDependentList;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class VectorToMatrix {
	private final Kernel kernel;

	public VectorToMatrix(Kernel kernel) {
		this.kernel = kernel;
	}

	public String build(StringTemplate template, ExpressionNode expressionNode) {
		MyVecNDNode vecNode =  (MyVecNDNode)(expressionNode.getLeft());

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

	public static String[] getLaTeXValues(ExpressionNode expressionNode, StringTemplate tpl) {
		MyVecNDNode vecNode = (MyVecNDNode) expressionNode.getLeft();
		List<String> list = new ArrayList<>();
		if (vecNode != null) {
			list.add(vecNode.getX().toLaTeXString(true, tpl));
			list.add(vecNode.getY().toLaTeXString(true, tpl));
			ExpressionValue z = vecNode.getZ();
			list.add(z != null ? z.toLaTeXString(true, tpl): "");
		}

		String[] array = new String[3];
		return list.toArray(array);
	}

	public static boolean isEditable(GeoElementND geo) {
		if (geo.getParentAlgorithm() instanceof AlgoDependentList) {
			AlgoElement algo = geo.getParentAlgorithm();
			for (int i = 0; i < algo.getInputLength(); i++) {
				GeoElementND element = algo.getInput(i);
				if (!element.isIndependent() && !(element
						.getParentAlgorithm() instanceof AlgoDependentList)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
