package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.kernelND.GeoCoordSys;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CommandProcessor;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoPlane3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.GeoVector3D;
import geogebra3D.kernel3D.Kernel3D;

public class CmdCylinderInfinite extends CmdCylinder {
	
	
	
	public CmdCylinderInfinite(Kernel kernel) {
		super(kernel);
	}

	

	protected GeoElement[] cylinderPointPointRadius(Command c, GeoPointND p1, GeoPointND p2, NumberValue r){
		return new GeoElement[] {kernel.getManager3D().Cylinder(
				c.getLabel(),p1,p2,r)};
	}
	
	protected MyError argErr(GeoElement geo){
		return argErr(app,"CylinderInfinite",geo);
	}
	
	protected MyError argNumErr(int n){
		return argNumErr(app,"CylinderInfinite",n);
	}
	
	
	
	
	
}
