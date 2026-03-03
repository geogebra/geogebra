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
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Operation;
import org.geogebra.editor.share.util.Unicode;

/**
 * Detects cases of angle conversion.
 * <ul>
 * <li>in radian mode, anything that contains degree constant outside
 *   trig argument is disallowed</li>
 * <li> in degree or DMS mode, degrees are only allowed if
 *   <ul>
 *     <li> the result is an angle and the angle dimension is 1 (e.g. 60deg + 30deg)</li>
 *     <li> the result is a number and the angle dimension is 0 (e.g. 60deg/30deg -> 2)</li>
 *   </ul>
 * </li>
 * </ul>
 */
public class AngleConversionFilter implements AlgebraOutputFilter {

	@Override
	public boolean isAllowed(GeoElementND element) {
		ExpressionNode definition = element.getDefinition();
		if (definition == null) {
			return true; // do not filter commands, sliders etc.
		}
		if (element.getKernel().getAngleUnit() == Kernel.ANGLE_RADIANT) {
			// degrees allowed in arguments of trig functions
			ExpressionValue trigFreeDefinition = definition
					.deepCopy(element.getKernel()).traverse(this::skipTrig);
			return trigFreeDefinition.none(this::isDegree);
		}
		if (definition.none(this::isDegree)) {
			return true; // no angle computations involved
		}
		return isSimpleDegreeOrScalarExpression(element, getAngleDimension(definition));
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

	/*
	 * Checks if the expression is scalar (1+1,sin(5deg), 1deg/deg ...) or simple angle (1deg+1deg).
	 */
	private boolean isSimpleDegreeOrScalarExpression(GeoElementND element, Integer angleDimension) {
		if (angleDimension == null) {
			return false; // mixing angles and scalars, e.g. 1deg + 2
		}
		return switch (angleDimension) {
			case 0 -> !(element instanceof GeoAngle); // don't allow printing scalar as angle
			case 1 -> element instanceof GeoAngle; // don't allow printing angle as scalar
			default -> false; // e.g. 1/deg
		};
	}

	private boolean isDegree(ExpressionValue s) {
		return Unicode.DEGREE_STRING.equals(s.toString(
				StringTemplate.defaultTemplate)) || s instanceof GeoAngle;
	}

	private Integer getAngleDimension(@CheckForNull ExpressionValue expression) {
		if (expression == null) {
			return null;
		}

		return expression.getAngleDimension();
	}
}
