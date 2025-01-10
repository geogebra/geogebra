package org.geogebra.common.geogebra3D.euclidian3D.draw;

/**
 * 
 * Index for a trace occurrence
 *
 */
public class TraceIndex {
	/** geometry index */
	public int geom;
	/** surface index */
	public int surface;

	/**
	 * @param geom
	 *            geometry index
	 * @param surface
	 *            surface index
	 */
	public TraceIndex(int geom, int surface) {
		this.geom = geom;
		this.surface = surface;
	}

}