package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Algo for infinite cylinder (point, direction, radius)
 * 
 * @author matthieu
 *
 */
public class AlgoCylinderInfinitePointVectorNumber
		extends AlgoQuadricPointVectorNumber {

	/**
	 * @param c
	 *            constructor
	 * @param label
	 *            label
	 * @param origin
	 *            origin
	 * @param direction
	 *            direction
	 * @param radius
	 *            radius
	 */
	public AlgoCylinderInfinitePointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, GeoNumberValue radius) {
		super(c, label, origin, direction, radius,
				new AlgoQuadricComputerCylinder());
	}

	@Override
	final protected String getPlainName() {
		return "InfiniteCylinderPointAVectorBNumberC";
	}

	@Override
	public Commands getClassName() {
		return Commands.CylinderInfinite;
	}

}
