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

package org.geogebra.common.kernel.arithmetic.traversing;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.plugin.Operation;

import com.google.j2objc.annotations.Weak;

public class SqrtMinusOneReplacer implements Traversing {
	@Weak
	private final Kernel kernel;

	public SqrtMinusOneReplacer(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev.isExpressionNode()) {
			ExpressionNode node = ev.wrap();
			ExpressionValue left = node.getLeft();
			if (node.getOperation() == Operation.SQRT
					&& left.isNumberValue() && left.isConstant()) {
				if (left.evaluateDouble() == -1) {
					return kernel.getImaginaryUnit();
				}
			}
		}
		if (ev instanceof Variable
				&& "i".equals(ev.toString(StringTemplate.xmlTemplate))) {
			return kernel.getImaginaryUnit();
		}
		return ev;
	}
}
