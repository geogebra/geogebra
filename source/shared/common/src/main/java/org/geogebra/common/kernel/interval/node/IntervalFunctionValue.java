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

package org.geogebra.common.kernel.interval.node;

import org.geogebra.common.kernel.interval.Interval;

/**
 * Function value (an interval) in the tree as leaf.
 */
public class IntervalFunctionValue implements IntervalExpressionValue {
	private final Interval interval;

	/**
	 *
	 * @param interval the value.
	 */
	public IntervalFunctionValue(Interval interval) {
		this.interval = interval;
		interval.setPrecision(0);
	}

	@Override
	public void set(Interval interval) {
		this.interval.set(interval);
	}

	@Override
	public void set(double value) {
		this.interval.set(value, value);
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public IntervalExpressionNode asExpressionNode() {
		return null;
	}

	@Override
	public Interval value() {
		return interval;
	}

	@Override
	public boolean hasFunctionVariable() {
		return false;
	}

	@Override
	public IntervalNode simplify() {
		return this;
	}

	@Override
	public String toString() {
		return interval.toShortString();
	}
}
