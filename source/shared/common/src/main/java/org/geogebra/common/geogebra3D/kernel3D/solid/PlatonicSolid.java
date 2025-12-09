/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
