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

package org.geogebra.common.euclidian.plot.implicit;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class BernsteinBoundingBox implements Splittable<BernsteinBoundingBox> {
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	private static final BernsteinBoundingBoxPool pool = new BernsteinBoundingBoxPool();

	/**
	 *
	 * @param x1 left x coordinate.
	 * @param y1 top y coordinate.
	 * @param x2 right x coordinate.
	 * @param y2 bottom y coordinate.
	 */
	public BernsteinBoundingBox(double x1, double y1, double x2, double y2) {
		set(x1, y1, x2, y2);
	}

	public BernsteinBoundingBox() {
		// for pool initialization
	}

	/**
	 * Sets the box coordinates
	 *
	 * @param x1 left x coordinate.
	 * @param y1 top y coordinate.
	 * @param x2 right x coordinate.
	 * @param y2 bottom y coordinate.
	 */
	public void set(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	/**
	 *
	 * @param limits of the bounding box to get coordinates from.
	 */
	public BernsteinBoundingBox(BoundsRectangle limits) {
		this(limits.getXmin(), limits.getYmin(), limits.getXmax(), limits.getYmax());
	}

	@Override
	public BernsteinBoundingBox[] split() {
		BernsteinBoundingBox[] boxes = new BernsteinBoundingBox[4];
		double xHalf = (x1 + x2) / 2;
		double yHalf = (y1 + y2) / 2;
		boxes[0] = pool.request(x1, y1, xHalf, yHalf);
		boxes[1] = pool.request(xHalf, y1, x2, yHalf);
		boxes[2] = pool.request(x1, yHalf, xHalf, y2);
		boxes[3] = pool.request(xHalf, yHalf, x2, y2);
		return boxes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof BernsteinBoundingBox)) {
			return false;
		}

		BernsteinBoundingBox that = (BernsteinBoundingBox) o;
		return x1 == that.x1
				&& y1 == that.y1
				&& x2 == that.x2
				&& y2 == that.y2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x1, y1, x2, y2);
	}

	@Override
	public String toString() {
		return "Box{x1=" + x1
				+ ", y1=" + y1
				+ ", y2=" + y2
				+ ", x2=" + x2
				+ '}';
	}

	/**
	 * @return left
	 */
	public double x1() {
		return x1;
	}

	/**
	 * @return top
	 */
	public double y1() {
		return y1;
	}

	/**
	 * @return right
	 */
	public double x2() {
		return x2;
	}

	/**
	 * @return bottom
	 */
	public double y2() {
		return y2;
	}

	public double getWidth() {
		return x2 - x1;
	}

	public double getHeight() {
		return y2 - y1;
	}

	/**
	 * Release this to a pool.
	 */
	public void release() {
		pool.release(this);
	}

}
