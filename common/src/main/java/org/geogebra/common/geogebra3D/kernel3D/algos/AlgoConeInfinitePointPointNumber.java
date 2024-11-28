package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for infinite cone (point, direction, angle)
 * 
 * @author matthieu
 *
 */
public class AlgoConeInfinitePointPointNumber
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
	 * @param angle
	 *            angle
	 */
	public AlgoConeInfinitePointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, GeoNumberValue angle) {
		super(c, label, origin, secondPoint, angle,
				new AlgoQuadricComputerCone());
	}

	@Override
	final protected String getPlainName() {
		return "InfiniteConePointsABNumberC";
	}

	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
