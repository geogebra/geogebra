package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Algo for infinite cylinder (point, direction, radius)
 * @author matthieu
 *
 */
public class AlgoCylinderInfinitePointVectorNumber extends
		AlgoQuadricPointVectorNumber {

	/**
	 * @param c constructor
	 * @param label label
	 * @param origin origin
	 * @param direction direction
	 * @param radius radius
	 */
	public AlgoCylinderInfinitePointVectorNumber(Construction c, String label,
			GeoPointND origin, GeoVectorND direction, NumberValue radius) {
		super(c, label, origin, direction, radius, new AlgoQuadricComputerCylinder());
	}
	
	@Override
	final protected String getPlainName(){
		return "CylinderInfiniteFromQuadricPointAVectorBNumberC";
	}
	
	@Override
	public Commands getClassName() {
		return Commands.CylinderInfinite;
	}
	
}
