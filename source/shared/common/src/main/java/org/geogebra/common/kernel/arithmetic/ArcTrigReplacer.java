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

package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.plugin.Operation;

/**
 * Replaces asin(0.5) with asind(0.5)
 *
 */
public final class ArcTrigReplacer implements Traversing {

	private static ArcTrigReplacer replacer = new ArcTrigReplacer();

	private ArcTrigReplacer() {
		// singleton constructor
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) ev;
			Operation op = en.getOperation();
			Operation newOp = getDegreeInverseTrigOp(op);

			if (newOp != op) {
				en.setOperation(newOp);
			}
		}
		return ev;
	}

	/**
	 * Maps asin -&gt; asind, acos -&gt; acosd, uses input operation as fallback
	 * @param op operation
	 * @return inverse trig function that produces degrees or input operation
	 */
	public static Operation getDegreeInverseTrigOp(Operation op) {
		if (op == null) {
			return null;
		}
		switch (op) {
			default:
				// do nothing
				return op;
			case ARCSIN:
				return Operation.ARCSIND;
			case ARCCOS:
				return Operation.ARCCOSD;
			case ARCTAN:
				return Operation.ARCTAND;
			case ARCTAN2:
				return Operation.ARCTAN2D;
		}
	}

	/**
	 * @return replacer
	 */
	public static ArcTrigReplacer getReplacer() {
		return replacer;
	}
}
