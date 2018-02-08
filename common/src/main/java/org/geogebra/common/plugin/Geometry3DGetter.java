package org.geogebra.common.plugin;

/**
 * interface to get geometries from 3D renderer
 */
public interface Geometry3DGetter {

	/**
	 * start new geometry
	 */
	public void startGeometry();

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
