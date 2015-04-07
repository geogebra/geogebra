package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoQuadricNDConstants;

/**
 * Algo for cylinder from a conic and a height
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricLimitedConicHeightCylinder extends
		AlgoQuadricLimitedConicHeight {

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
			String[] labels, GeoConicND bottom, NumberValue height) {
		super(c, labels, bottom, height, GeoQuadricNDConstants.QUADRIC_CYLINDER);
	}

	@Override
	protected void setQuadric(Coords o1, Coords o2, Coords d, double r,
			double min, double max) {
		getQuadric().setCylinder(o1, d, r, min, max);
	}

	@Override
	public Commands getClassName() {
		return Commands.Cylinder;
	}

	// TODO Consider locusequability

}
