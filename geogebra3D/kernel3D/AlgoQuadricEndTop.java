package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.Matrix.Coords;

/**
 * Compute the bottom of a limited quadric
 * @author matthieu
 *
 */
public class AlgoQuadricEndTop extends AlgoQuadricEnd {

	public AlgoQuadricEndTop(Construction cons, String label, GeoQuadric3DLimited quadric) {
		super(cons, label, quadric);
	}

	protected Coords getOrigin(Coords o1, Coords o2) {
		return o2;
	}

	protected Coords getV1(Coords v1) {
		return v1;
	}


	public String getClassName() {
		return "AlgoQuadricEndTop";
	}

}
