package org.geogebra.common.geogebra3D.euclidian3D.draw;

import org.geogebra.common.kernel.Matrix.Coords;

public class TraceIndex {
	public int geom, surface;
	public Coords center;

	public TraceIndex(int geom, int surface) {
		this.geom = geom;
		this.surface = surface;
	}

	public TraceIndex(int geom, int surface, Coords center) {
		this.geom = geom;
		this.surface = surface;
		this.center = center;
	}

}