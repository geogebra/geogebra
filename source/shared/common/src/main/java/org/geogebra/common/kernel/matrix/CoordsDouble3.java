package org.geogebra.common.kernel.matrix;

import org.geogebra.common.util.DoubleUtil;

/**
 * class for 3 double (x, y, z)
 * 
 * @author mathieu
 *
 */
public class CoordsDouble3 extends Coords3 {

	/** undefined vector */
	public static final Coords3 UNDEFINED = new CoordsDouble3(0f, 0f, 0f) {
		@Override
		public boolean isNotFinalUndefined() {
			return false;
		}

		@Override
		public boolean isFinalUndefined() {
			return true;
		}
	};

	public double x;
	public double y;
	public double z;

	/**
	 * constructor
	 */
	public CoordsDouble3() {
	}

	/**
	 * constructor
	 * 
	 * @param x
	 *            x coord
	 * @param y
	 *            y coord
	 * @param z
	 *            z coord
	 */
	public CoordsDouble3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	final public boolean isDefined() {
		return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z);
	}

	@Override
	final public CoordsDouble3 copyVector() {

		return new CoordsDouble3(x, y, z);

	}

	@Override
	final public void addInside(Coords3 v) {
		x += v.getXd();
		y += v.getYd();
		z += v.getZd();
	}

	@Override
	final public void mulInside(float v) {
		x *= v;
		y *= v;
		z *= v;
	}

	@Override
	final public void mulInside(double v) {
		x *= v;
		y *= v;
		z *= v;
	}

	@Override
	public void normalizeIfPossible() {
		double l = Math.sqrt(x * x + y * y + z * z);
		if (!DoubleUtil.isZero(l)) {
			mulInside(1 / l);
		}
	}

	@Override
	final public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	final public void set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;

	}

	@Override
	final public double getXd() {
		return x;
	}

	@Override
	final public double getYd() {
		return y;
	}

	@Override
	final public double getZd() {
		return z;
	}

	@Override
	final public float getXf() {
		return (float) x;
	}

	@Override
	final public float getYf() {
		return (float) y;
	}

	@Override
	final public float getZf() {
		return (float) z;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(x);
		ret.append(',');
		ret.append(y);
		ret.append(',');
		ret.append(z);
		return ret.toString();
	}

	@Override
	public void mulInside(double a, double b, double c) {
		x *= a;
		y *= b;
		z *= c;
	}

}
