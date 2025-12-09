/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
import org.geogebra.editor.share.util.Unicode;

public class AlgebraConversionFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElementND element) {
		if (element.getKernel().getAngleUnit() == Kernel.ANGLE_RADIANT) {
			return element.getDefinition() == null
					|| element.getDefinition().deepCopy(element.getKernel())
							.traverse(this::skipTrig).none(this::isDegree);
		}
		return element.getDefinition() == null
				|| element.getDefinition().none(this::isDegree)
				|| !wrongAngleDimension(element, getAngleDimension(element.getDefinition()));
	}

	private ExpressionValue skipTrig(ExpressionValue value) {
		if (value.isExpressionNode()) {
			Operation op = ((ExpressionNode) value).getOperation();
			if (op.hasDegreeInput()) {
				return ((ExpressionNode) value).getKernel().getEulerNumber();
			}
		}
		return value;
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
