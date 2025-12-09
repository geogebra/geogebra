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

package org.geogebra.common.euclidian.plot.interval;

import java.util.Objects;

/**
 * Entry for logging IntervalPath methods,
 * like reset(), moveTo(x, y) and lineTo(x, y).
 */
public class IntervalPathMockEntry {
	final PathOperation operation;
	double x;
	double y;

	public enum PathOperation {
		MOVE_TO("MOVE_TO"),
		LINE_TO("LINE_TO"),
		RESET("R");

		private final String text;

		PathOperation(String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public IntervalPathMockEntry() {
		operation = PathOperation.RESET;
	}

	/**
	 *
	 * @param operation to log.
	 * @param x coordinate.
	 * @param y coordinate.
	 */
	public IntervalPathMockEntry(PathOperation operation, double x, double y) {
		this.operation = operation;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return  operation == PathOperation.RESET
				? operation.toString()
				: operation + " " + x + ", "  + y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IntervalPathMockEntry that = (IntervalPathMockEntry) o;
		return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0
				&& operation == that.operation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(operation, x, y);
	}
}
