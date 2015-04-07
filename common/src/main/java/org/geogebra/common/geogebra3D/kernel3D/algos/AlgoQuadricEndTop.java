package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;

/**
 * Compute the bottom of a limited quadric
 * 
 * @author matthieu
 *
 */
public class AlgoQuadricEndTop extends AlgoQuadricEnd {

	public AlgoQuadricEndTop(Construction cons, String label,
			GeoQuadric3DLimited quadric) {
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
	public Commands getClassName() {
		return Commands.Top;
	}

	// TODO Consider locusequability

}
