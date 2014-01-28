package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdCylinderInfinite extends CmdCylinder {
	
	
	
	public CmdCylinderInfinite(Kernel kernel) {
		super(kernel);
	}

	

	protected GeoElement[] cylinderPointPointRadius(Command c, GeoPointND p1, GeoPointND p2, NumberValue r) {
		return new GeoElement[] {kernelA.getManager3D().Cylinder(
				c.getLabel(), p1, p2, r)};
	}
	
	protected MyError argErr(GeoElement geo, Command c) {
		return argErr(app, c.getName(), geo);
	}
	
	protected MyError argNumErr(int n, Command c) {
		return argNumErr(app, c.getName(), n);
	}
	
	
	
	
	
}
