package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.locusequ.EquationElement;
import geogebra.common.kernel.locusequ.EquationScope;

/**
 * Compute the bottom of a limited quadric
 * @author matthieu
 *
 */
public class AlgoQuadricEndTop extends AlgoQuadricEnd {

	public AlgoQuadricEndTop(Construction cons, String label, GeoQuadric3DLimited quadric) {
		super(cons, label, quadric);
	}
	
	public AlgoQuadricEndTop(Construction cons, GeoQuadric3DLimited quadric) {
		super(cons, quadric);
	}

	@Override
	protected Coords getOrigin(Coords o1, Coords o2) {
		return o2;
	}

	@Override
	protected Coords getV1(Coords v1) {
		return v1;
	}


	@Override
	public Algos getClassName() {
		return Algos.AlgoQuadricEndTop;
	}

	// TODO Consider locusequability

}
