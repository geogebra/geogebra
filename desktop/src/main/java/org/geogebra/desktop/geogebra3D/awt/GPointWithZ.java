package org.geogebra.desktop.geogebra3D.awt;

/** Class for integer tuples **/
public class GPointWithZ extends org.geogebra.common.awt.GPoint {

	public GPointWithZ() {
		z = 0;
	}

	public GPointWithZ(int x, int y, int z) {
		super(x, y);
		this.z = z;
	}

	/** z-coordinate **/
	public int z = 0;

	/**
	 * Set x and y at the same time
	 * 
	 * @param x
	 *            x-coord
	 * @param y
	 *            y-coord
	 * @param z
	 *            z-coord
	 */
	public void setLocation(int x, int y, int z) {
		super.setLocation(x, y);
		this.z = z;
	}

	/**
	 * Take both coords from a point
	 * 
	 * @param p
	 *            point
	 */
	public void setLocation(GPointWithZ p) {
		super.setLocation(p);
		this.z = p.z;
	}

	public int getZ() {
		return z;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GPointWithZ))
			return false;
		return ((GPointWithZ) o).x == x && ((GPointWithZ) o).y == y
				&& ((GPointWithZ) o).z == z;
	}

	@Override
	public int hashCode() {
		return (super.hashCode() << 16) ^ z;
	}

	public double distance(GPointWithZ d) {
		return Math.sqrt((x - d.x) * (x - d.x) + (y - d.y) * (y - d.y)
				+ (z - d.z) * (z - d.z));
	}

	public double distance(double dx, double dy, double dz) {
		return (int) Math.sqrt((x - dx) * (x - dx) + (y - dy) * (y - dy)
				+ (z - dz) * (z - dz));
	}

	@Override
	public String toString() {
		return x + " : " + y + " : " + z;
	}

}
