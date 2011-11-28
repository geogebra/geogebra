package geogebra3D.kernel3D;

import geogebra.common.kernel.Matrix.Coords;
import geogebra.kernel.Construction;

/**
 * Compute the bottom of a limited quadric
 * @author matthieu
 *
 */
public class AlgoQuadricEndBottom extends AlgoQuadricEnd {

	public AlgoQuadricEndBottom(Construction cons, String label, GeoQuadric3DLimited quadric) {
		super(cons, label, quadric);
	}

	protected Coords getOrigin(Coords o1, Coords o2) {
		return o1;
	}

	protected Coords getV1(Coords v1) {
		return v1.mul(-1);
	}


	public String getClassName() {
		return "AlgoQuadricEndBottom";
	}

}
