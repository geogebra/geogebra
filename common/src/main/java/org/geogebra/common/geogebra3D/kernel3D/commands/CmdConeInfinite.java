package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Processor for InfiniteCone command
 *
 */
public class CmdConeInfinite extends CmdCone {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdConeInfinite(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] conePointPointRadius(Command c, GeoPointND p1,
			GeoPointND p2, NumberValue r) {
		return new GeoElement[] { kernelA.getManager3D().Cone(c.getLabel(), p1,
				p2, r) };
	}

}
