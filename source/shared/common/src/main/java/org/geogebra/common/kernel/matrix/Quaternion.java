package org.geogebra.common.kernel.matrix;

/**
 * Simple class for quaternions operations
 * 
 * @author mathieu
 *
 */
public class Quaternion {

	private double x;
	private double y;
	private double z;
	private double w;

	/**
	 * constructor
	 * 
	 * @param x
	 *            x coord (vector part)
	 * @param y
	 *            y coord (vector part)
	 * @param z
	 *            z coord (vector part)
	 * @param w
	 *            scalar part
	 */
	public Quaternion(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * constructor
	 */
	public Quaternion() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}

	/**
	 * construct a quaternion corresponding to rotations around X then Y
	 * 
	 * @param rotX
	 *            rotation around X
	 * @param rotZ
	 *            rotation around Z
	 */
	public Quaternion(double rotX, double rotZ) {

		double cx = Math.cos(rotX / 2);
		double cz = Math.cos(rotZ / 2);
		double sx = Math.sin(rotX / 2);
		double sz = Math.sin(rotZ / 2);

		w = cx * cz;
		x = sx * cz;
		y = sx * sz;
		z = cx * sz;
	}

	/**
	 * set the values
	 * 
	 * @param values
	 *            values
	 */
	public void set(double[] values) {
		this.x = values[0];
		this.y = values[1];
		this.z = values[2];
		this.w = values[3];
	}

	/**
	 * set the values
	 * 
	 * @param q
	 *            values
	 */
	public void set(Quaternion q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.w = q.w;
	}

	/**
	 * 
	 * @return inverse
	 */
	public Quaternion inverse() {
		double norm2 = x * x + y * y + z * z + w * w;
		return new Quaternion(-x / norm2, -y / norm2, -z / norm2, w / norm2);
	}

	/**
	 * 
	 * @param q
	 *            quaternion
	 * @return this**q
	 */
	public Quaternion multiply(Quaternion q) {

		double mx = w * q.x + x * q.w + y * q.z - z * q.y;
		double my = w * q.y - x * q.z + y * q.w + z * q.x;
		double mz = w * q.z + x * q.y - y * q.x + z * q.w;
		double mw = w * q.w - x * q.x - y * q.y - z * q.z;

		return new Quaternion(mx, my, mz, mw);

	}

	/**
	 * 
	 * @param q
	 *            quaternion
	 * @return (this^(-1))**q
	 */
	public Quaternion leftDivide(Quaternion q) {
		return this.inverse().multiply(q);
	}

	public double getAngleX() {
		return Math.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
	}

	public double getAngleY() {
		return Math.asin(2 * (w * y - z * x));
	}

	public double getAngleZ() {
		return Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
	}

	@Override
	public String toString() {
		return "v=(" + x + "," + y + "," + z + ")  scalar=" + w;
	}

	/**
	 * 
	 * @return 3x3 rotation matrix (for unit quaternion)
	 */
	public CoordMatrix getRotMatrix() {
		CoordMatrix ret = new CoordMatrix(3, 3);

		ret.set(1, 1, 1 - 2 * (y * y + z * z));
		ret.set(1, 2, 2 * (x * y - w * z));
		ret.set(1, 3, 2 * (w * y + x * z));

		ret.set(2, 1, 2 * (x * y + w * z));
		ret.set(2, 2, 1 - 2 * (x * x + z * z));
		ret.set(2, 3, 2 * (y * z - w * x));

		ret.set(3, 1, 2 * (x * z - w * y));
		ret.set(3, 2, 2 * (y * z + w * x));
		ret.set(3, 3, 1 - 2 * (x * x + y * y));

		return ret;

	}

	/**
	 * 
	 * @return (x,y,z) vector part of the quaternion
	 */
	public Coords getVector() {
		return new Coords(x, y, z);
	}

	/**
	 * 
	 * @return scalar value
	 */
	public double getScalar() {
		return w;
	}

	/**
	 * set vector v to (x,y,z)
	 * 
	 * @param v
	 *            3D vector
	 */
	public void setVector(Coords v) {
		x = v.getX();
		y = v.getY();
		z = v.getZ();
	}

	/**
	 * set this quaternion to undefined
	 */
	public void setUndefined() {
		x = Double.NaN;
	}

	/**
	 * 
	 * @return true if this quaternion is defined
	 */
	public boolean isDefined() {
		return Double.isNaN(x);
	}

	/**
	 * assuming angle t between this and q verifies cos(t) = 2 dotproduct(this,
	 * q)^2 - 1 we return 1 - dotproduct(this, q)^2 = (1-cos(t))/2
	 * 
	 * @param q
	 *            quaternion
	 * @return distance between this and q
	 */
	public double distance(Quaternion q) {

		double norm2 = x * x + y * y + z * z + w * w;
		double qnorm2 = q.x * q.x + q.y * q.y + q.z * q.z + q.w * q.w;

		double dot = x * q.x + y * q.y + z * q.z + w * q.w;
		return 1 - dot * dot / (norm2 * qnorm2);
	}

}
