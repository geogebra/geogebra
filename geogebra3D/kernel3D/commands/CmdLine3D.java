package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdLine;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;
import geogebra.main.MyError;
import geogebra3D.kernel3D.GeoElement3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Line[ <GeoPoint3D>, <GeoPoint3D> ] or CmdLine
 */
public class CmdLine3D extends CmdLine {
	

	public CmdLine3D(Kernel kernel) {
		super(kernel);	
	}

	

	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		if (n==2) {
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() || arg[1].isGeoElement3D()){

				GeoElement geo0 = (GeoElement) arg[0];
				GeoElement geo1 = (GeoElement) arg[1];

				
				if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1.isGeoPoint()))) { // line between two 3D points
					GeoElement[] ret =
					{
							kernel.getManager3D().Line3D(
									c.getLabel(),
									(GeoPointND) geo0,
									(GeoPointND) geo1)};
					return ret;
				}else if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1.isGeoVector()))) { // line directed
					GeoElement[] ret =
					{
							(GeoElement) kernel.getManager3D().Line3D(
									c.getLabel(),
									(GeoPointND) geo0,
									(GeoVectorND) geo1)};
					return ret;
					
					
				}else if ((ok[0] = (geo0.isGeoPoint()))
						&& (ok[1] = (geo1 instanceof GeoLineND))) { // line parallel
					GeoElement[] ret =
					{
							(GeoElement) kernel.getManager3D().Line3D(
									c.getLabel(),
									(GeoPointND) geo0,
									(GeoLineND) geo1)};
					return ret;
				}
			}
		}

		return super.process(c);
	}

}
