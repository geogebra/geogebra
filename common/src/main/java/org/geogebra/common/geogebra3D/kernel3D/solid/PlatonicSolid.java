package org.geogebra.common.geogebra3D.kernel3D.solid;

import org.geogebra.common.kernel.Matrix.Coords;

public class PlatonicSolid {

	private int[][] faces;
	private Coords[] coords;

	
	public void set(Coords[] coords, int[][] faces) {
		this.coords = coords;
		this.faces = faces;
	}

	public int getVertexCount() {
		return coords.length;
	}

	public Coords[] getVertices() {
		return coords;
	}

	public int[][] getFaces() {
		return faces;
	}

}
