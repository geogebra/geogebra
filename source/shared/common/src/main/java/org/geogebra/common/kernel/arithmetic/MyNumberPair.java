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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;

/**
 * 
 * @author Michael
 */
public class MyNumberPair extends MyVecNode {
	/**
	 * Creates new number pair
	 * 
	 * @param kernel
	 *            kernel
	 * @param en
	 *            first number
	 * @param en2
	 *            second number
	 */
	public MyNumberPair(Kernel kernel, ExpressionValue en,
			ExpressionValue en2) {
		super(kernel, en, en2);
	}

	/**
	 * @param kernel
	 *            kernel
	 */
	public MyNumberPair(Kernel kernel) {
		super(kernel, new MyDouble(kernel), new MyDouble(kernel));
	}

	@Override
	public MyNumberPair deepCopy(Kernel kernel1) {
		return new MyNumberPair(kernel1, x.deepCopy(kernel1),
				y.deepCopy(kernel1));
	}

	@Override
	final public String toString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append(x.toString(tpl));
		sb.append(", ");
		sb.append(y.toString(tpl));

		return sb.toString();
	}

	@Override
	final public String toValueString(StringTemplate tpl) {
		StringBuilder sb = new StringBuilder();
		sb.append(x.toValueString(tpl));
		sb.append(", ");
		sb.append(y.toValueString(tpl));

		return sb.toString();
	}

	@Override
	final public ExpressionValue traverse(Traversing t) {
		ExpressionValue v = t.process(this);
		if (v != this) {
			return v;
		}
		x = x.traverse(t);
		y = y.traverse(t);
		return this;
	}

	/**
	 * @param x
	 *            x coordinate
	 */
	public void setX(ExpressionValue x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            y coordinate
	 */
	public void setY(ExpressionValue y) {
		this.y = y;
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return symbolic ? toString(tpl) : toValueString(tpl);
	}

}
