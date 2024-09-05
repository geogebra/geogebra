package org.geogebra.common.euclidian.plot.implicit;

import java.util.Objects;

import org.geogebra.common.kernel.arithmetic.BoundsRectangle;
import org.geogebra.common.kernel.arithmetic.Splittable;

public class BernsteinBoundingBox implements Splittable<BernsteinBoundingBox> {
	private final double x1;
	private final double y1;
	private final double x2;
	private final double y2;
	private final double xHalf;
	private final double yHalf;

	public BernsteinBoundingBox(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		xHalf = x1 + ((x2 - x1) / 2);
		yHalf = y1 + ((y2 - y1) / 2);
	}

	public BernsteinBoundingBox(BoundsRectangle limits) {
		this(limits.getXmin(), limits.getYmin(), limits.getXmax(), limits.getYmax());
	}

	@Override
	public BernsteinBoundingBox[] split() {
		BernsteinBoundingBox[] boxes = new BernsteinBoundingBox[4];
		boxes[0] = new BernsteinBoundingBox(x1, y1, xHalf, yHalf);
		boxes[1] = new BernsteinBoundingBox(xHalf, y1, x2, yHalf);
		boxes[2] = new BernsteinBoundingBox(x1, yHalf, xHalf, y2);
		boxes[3] = new BernsteinBoundingBox(xHalf, yHalf, x2, y2);
		return boxes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BernsteinBoundingBox)) return false;
		BernsteinBoundingBox that = (BernsteinBoundingBox) o;
		return Double.compare(x1, that.x1) == 0
				&& Double.compare(y1, that.y1) == 0
				&& Double.compare(x2, that.x2) == 0
				&& Double.compare(y2, that.y2) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x1, y1, x2, y2);
	}

	@Override
	public String toString() {
		return "Box{x1=" + x1 +
				", y1=" + y1 +
				", y2=" + y2 +
				", x2=" + x2 +
				'}';
	}

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public double getX2() {
		return x2;
	}

	public double getY2() {
		return y2;
	}

	public double getXHalf() {
		return xHalf;
	}

	public double getYHalf() {
		return yHalf;
	}

	public double getWidth() {
		return x2 - x1;
	}

	public double getHeight() {
		return y2 - y1;
	}
}
