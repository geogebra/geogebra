package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.kernelND.GeoConicND;

/**
 * Class for vertices of a 3D conic
 * 
 * @author mathieu
 *
 */
public class AlgoVertexConic3D extends AlgoVertexConic {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param labels
	 *            output labels
	 * @param c
	 *            conic
	 */
	public AlgoVertexConic3D(Construction cons, String[] labels, GeoConicND c) {
		super(cons, labels, c);
	}

	@Override
	protected void createVertex(Construction cons1) {
		vertex = new GeoPoint3D[4];
		for (int i = 0; i < vertex.length; i++) {
			vertex[i] = new GeoPoint3D(cons1);
		}
	}

	@Override
	protected void setCoords(int i, double x, double y) {
		((GeoPoint3D) vertex[i]).setCoords(c.getCoordSys().getPoint(x, y));
	}
}
