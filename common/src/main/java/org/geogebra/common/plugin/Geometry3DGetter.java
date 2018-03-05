package org.geogebra.common.plugin;

/**
 * interface to get geometries from 3D renderer
 */
public interface Geometry3DGetter {

	/**
	 * geometry type so the getter can sort in several parts
	 */
	public enum GeometryType {
		/** geometry from an axis */
		AXIS,
		/** geometry from a surface */
		SURFACE,
		/** geometry from a curve or line */
		CURVE
	}

	/**
	 * 
	 * @param type
	 *            geometry type
	 * @return true if it handles the geometry type
	 */
	public boolean handles(GeometryType type);

	/**
	 * start new geometry
	 * 
	 * @param type
	 *            geometry type
	 */
	public void startGeometry(GeometryType type);

	/**
	 * add vertex, normal, color element
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param nx
	 * @param ny
	 * @param nz
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public void addVertexNormalColor(double x, double y, double z, double nx, double ny, double nz, double r, double g,
			double b, double a);

	/**
	 * add 3 indices (as triangle)
	 * 
	 * @param i1
	 * @param i2
	 * @param i3
	 */
	public void addTriangle(int i1, int i2, int i3);
}
