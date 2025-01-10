package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Algo for infinite cone (point, direction, angle)
 * 
 * @author matthieu
 *
 */
public class AlgoConeInfinitePointVectorNumber
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
	 * @param angle
	 *            angle
	 */
	public AlgoConeInfinitePointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, GeoNumberValue angle) {
		super(c, label, origin, direction, angle,
				new AlgoQuadricComputerCone());
	}

	@Override
	final protected String getPlainName() {
		return "InfiniteConePointAVectorBNumberC";
	}

	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
