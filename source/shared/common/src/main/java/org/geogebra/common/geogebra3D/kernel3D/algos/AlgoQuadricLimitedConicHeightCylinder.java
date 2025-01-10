package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Algo for cylinder from a conic and a height
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCylinder
		extends AlgoQuadricLimitedConicHeight {

	/**
	 * 
	 * @param c
	 *            construction
	 * @param labels
	 *            labels
	 * @param bottom
	 *            bottom side
	 * @param height
	 *            height
	 */
	public AlgoQuadricLimitedConicHeightCylinder(Construction c,
			String[] labels, GeoConicND bottom, GeoNumberValue height) {
		super(c, labels, bottom, height, getExtrusionType(bottom));
	}

	private static int getExtrusionType(GeoConicND type) {
		switch (type.getType()) {
		case GeoConicNDConstants.CONIC_HYPERBOLA:
			return GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER;
		case GeoConicNDConstants.CONIC_PARABOLA:
			return GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER;
		case GeoConicNDConstants.CONIC_DOUBLE_LINE:
			return GeoQuadricNDConstants.QUADRIC_PLANE;
		case GeoConicNDConstants.CONIC_INTERSECTING_LINES:
			return GeoQuadricNDConstants.QUADRIC_INTERSECTING_PLANES;
		default:
			return GeoQuadricNDConstants.QUADRIC_CYLINDER;
		}
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, Coords eigen,
			double r, double r2, double min, double max) {
		switch (getExtrusionType(getBottomFace())) {
		default:
			// do nothing
			break;
		case GeoQuadricNDConstants.QUADRIC_CYLINDER:
			getQuadric().setCylinder(o1, d, r, min, max);
			break;
		case GeoQuadricNDConstants.QUADRIC_HYPERBOLIC_CYLINDER:
			getQuadric().setHyperbolicCylinder(o1, d, r, min, max);
			break;
		case GeoQuadricNDConstants.QUADRIC_PARABOLIC_CYLINDER:
			getQuadric().setParabolicCylinder(o1, d, r, min, max);
			break;
		}

	}

	@Override
	public Commands getClassName() {
		return Commands.Cylinder;
	}

}
