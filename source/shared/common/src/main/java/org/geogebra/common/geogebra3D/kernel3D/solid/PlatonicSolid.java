package org.geogebra.common.geogebra3D.kernel3D.solid;

import org.geogebra.common.kernel.matrix.Coords;

/**
 * Specification of a solid
 */
public class PlatonicSolid {

	private int[][] faces;
	private Coords[] coords;

	/**
	 * @param coords
	 *            vertex coordinates
	 * @param faces
	 *            face descriptions
	 */
	public void set(Coords[] coords, int[][] faces) {
		this.coords = coords;
		this.faces = faces;
	}

	/**
	 * @return number of vertices
	 */
	public int getVertexCount() {
		return coords.length;
	}

	/**
	 * @return vertex coordinates
	 */
	public Coords[] getVertices() {
		return coords;
	}

	/**
	 * @return face descriptions
	 */
	public int[][] getFaces() {
		return faces;
	}

}
