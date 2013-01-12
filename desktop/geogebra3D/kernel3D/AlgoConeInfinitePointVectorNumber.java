package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Algo for infinite cone (point, direction, angle)
 * @author matthieu
 *
 */
public class AlgoConeInfinitePointVectorNumber extends
		AlgoQuadricPointVectorNumber {

	/**
	 * @param c constructor
	 * @param label label
	 * @param origin origin
	 * @param direction direction
	 * @param angle angle
	 */
	public AlgoConeInfinitePointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, NumberValue angle) {
		super(c, label, origin, direction, angle, new AlgoQuadricComputerCone());
	}
	
	@Override
	final protected String getPlainName(){
		return "ConeInfiniteFromQuadricPointAVectorBNumberC";
	}
	
	@Override
	public Commands getClassName() {
		return Commands.ConeInfinite;
	}

}
