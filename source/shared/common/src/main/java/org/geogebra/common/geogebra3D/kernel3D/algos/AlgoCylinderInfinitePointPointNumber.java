package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for infinite cylinder (point, direction, radius)
 * 
 * @author matthieu
 *
 */
public class AlgoCylinderInfinitePointPointNumber
		extends AlgoQuadricPointPointNumber {

	/**
	 * @param c
	 *            constructor
	 * @param label
	 *            label
	 * @param origin
	 *            origin
	 * @param secondPoint
	 *            second point
	 * @param radius
	 *            radius
	 */
	public AlgoCylinderInfinitePointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue radius) {
		super(c, label, origin, secondPoint, radius,
				new AlgoQuadricComputerCylinder());
	}

	@Override
	final protected String getPlainName() {
		return "InfiniteCylinderPointsABNumberC";
	}

	@Override
	public Commands getClassName() {
		return Commands.CylinderInfinite;
	}

}
