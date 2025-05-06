package org.geogebra.common.exam.restrictions.wtr;

import javax.annotation.CheckForNull;

import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;

import com.himamis.retex.editor.share.util.Unicode;

public class AlgebraConversionFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElementND element) {
		if (element.getKernel().getAngleUnit() == Kernel.ANGLE_RADIANT
				&& element.getDefinition() != null
				&& element.getDefinition().any(this::isDegree)) {
			return false;
		} else if (element.getKernel().getAngleUnit() != Kernel.ANGLE_RADIANT
				&& element.getDefinition() != null
				&& element.getDefinition().any(this::isDegree)
				&& wrongAngleDimension(element, getAngleDimension(element.getDefinition()))) {
			return false;
		}
		return true;
	}

	private boolean wrongAngleDimension(GeoElementND element, Integer angleDimension) {
		return angleDimension == null || angleDimension < 0 || angleDimension > 1
				|| !(element instanceof GeoAngle) && angleDimension == 1;
	}

	private boolean isDegree(ExpressionValue s) {
		return Unicode.DEGREE_STRING.equals(s.toString(
				StringTemplate.defaultTemplate)) || s instanceof GeoAngle;
	}

	private Integer getAngleDimension(@CheckForNull ExpressionValue expression) {
		if (expression == null) {
			return null;
		}
		if (expression instanceof NumberValue) {
			return ((NumberValue) expression).getAngleDim();
		}
		if (expression.isExpressionNode()) {
			ExpressionNode expressionNode = expression.wrap();
			Integer leftDimension = getAngleDimension(expressionNode.getLeft());
			Operation operation = expressionNode.getOperation();
			if (operation == Operation.NO_OPERATION) {
				return leftDimension;
			}
			Integer rightDimension = getAngleDimension(expressionNode.getRight());
			if (leftDimension == null || rightDimension == null) {
				return null;
			}
			switch (operation) {
			case PLUS:
			case MINUS:
				return leftDimension.equals(rightDimension) ? leftDimension : null;
			case MULTIPLY:
				return leftDimension + rightDimension;
			case DIVIDE:
				return leftDimension - rightDimension;
			default:
				if (operation.hasDegreeInput()) {
					return 0;
				}
				return null;
			}
		}
		return null;
	}
}
