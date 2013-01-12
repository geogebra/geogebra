package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for infinite cone (point, direction, angle)
 * @author matthieu
 *
 */
public class AlgoConeInfinitePointPointNumber extends
		AlgoQuadricPointPointNumber {

	/**
	 * @param c constructor
	 * @param label label
	 * @param origin origin
	 * @param secondPoint second point
	 * @param angle angle
	 */
	public AlgoConeInfinitePointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, NumberValue angle) {
		super(c, label, origin, secondPoint, angle, new AlgoQuadricComputerCone());
	}
	
	@Override
	final protected String getPlainName(){
		return "ConeInfiniteFromQuadricPointsABNumberC";
	}
	
	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
