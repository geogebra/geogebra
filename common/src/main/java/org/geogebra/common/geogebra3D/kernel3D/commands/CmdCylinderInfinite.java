package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * InfiniteCylinder processor
 *
 */
public class CmdCylinderInfinite extends CmdCylinder {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCylinderInfinite(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] cylinderPointPointRadius(Command c, GeoPointND p1,
			GeoPointND p2, NumberValue r) {
		return new GeoElement[] { kernelA.getManager3D().Cylinder(c.getLabel(),
				p1, p2, r) };
	}

}
