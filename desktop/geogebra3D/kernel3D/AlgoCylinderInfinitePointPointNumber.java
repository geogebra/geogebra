package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Algo for infinite cylinder (point, direction, radius)
 * @author matthieu
 *
 */
public class AlgoCylinderInfinitePointPointNumber extends
		AlgoQuadricPointPointNumber {

	/**
	 * @param c constructor
	 * @param label label
	 * @param origin origin
	 * @param secondPoint second point
	 * @param radius radius
	 */
	public AlgoCylinderInfinitePointPointNumber(Construction c, String label,
			GeoPointND origin, GeoPointND secondPoint, NumberValue radius) {
		super(c, label, origin, secondPoint, radius, new AlgoQuadricComputerCylinder());
	}
	
	@Override
	final protected String getPlainName(){
		return "CylinderInfiniteFromQuadricPointsABNumberC";
	}
	

	@Override
	public Commands getClassName() {
		return Commands.CylinderInfinite;
	}
	

}
