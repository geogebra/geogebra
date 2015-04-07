package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

public class CmdConeInfinite extends CmdCone {

	public CmdConeInfinite(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] conePointPointRadius(Command c, GeoPointND p1,
			GeoPointND p2, NumberValue r) {
		return new GeoElement[] { kernelA.getManager3D().Cone(c.getLabel(), p1,
				p2, r) };
	}

	@Override
	protected MyError argErr(GeoElement geo, Command c) {
		return argErr(app, c.getName(), geo);
	}

	@Override
	protected MyError argNumErr(int n, Command c) {
		return argNumErr(app, c.getName(), n);
	}

}
