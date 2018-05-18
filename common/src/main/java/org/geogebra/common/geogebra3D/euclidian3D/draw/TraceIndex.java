package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.Matrix.Coords;

public class TraceIndex {
	/** geometry index */
	public int geom;
	/** surface index */
	public int surface;
	/** center */
	public Coords center;

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

	/**
	 * @param geom
	 *            geometry index
	 * @param surface
	 *            surface index
	 * @param center
	 *            center
	 */
	public TraceIndex(int geom, int surface, Coords center) {
		this.geom = geom;
		this.surface = surface;
		this.center = center;
	}

}