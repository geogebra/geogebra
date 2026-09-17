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

package org.geogebra.common.kernel.implicit;

import org.geogebra.common.kernel.MyPoint;

/**
 * Test helper point with an attached string label.
 * <p>
 * Extends {@link MyPoint} and overrides {@link #toString()} to return the label,
 * making assertion diffs easier to read.
 * </p>
 */
public class SegmentEndPoint extends MyPoint {
	private final String label;

	/**
	 * Creates a labeled point for tests.
	 *
	 * @param label short identifier returned by {@link #toString()}
	 * @param x     x coordinate
	 * @param y     y coordinate
	 */
	SegmentEndPoint(String label, double x, double y) {
		super(x, y);
		this.label = label;
	}

	/**
	 * Returns the label to aid readable test failures.
	 *
	 * @return the label
	 */
	@Override
	public String toString() {
		return label;
	}
}
