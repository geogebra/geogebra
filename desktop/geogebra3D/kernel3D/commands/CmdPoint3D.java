package geogebra3D.kernel3D.commands;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPoint;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Kernel3D;



/*
 * Point[ <Path (3D)> ] or Point[ <Region (3D)> ] or CmdPoint
 */
public class CmdPoint3D extends CmdPoint {
	
	public CmdPoint3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;



		switch (n) {
		case 1 :
			arg = resArgs(c);
			if (arg[0].isGeoElement3D() ){
				//GeoElement3D geo0 = (GeoElement3D) arg[0];
				GeoElement geo0 = arg[0];
				if (ok[0] = (geo0.isPath())) {
					GeoElement[] ret =
					{ (GeoElement) kernel.getManager3D().Point3D(c.getLabel(), (Path) geo0)};
					return ret;
				}
				// if arg[0] isn't a Path, try to process it as a region (e.g. GeoPlane3D)
				if (ok[0] = (arg[0].isRegion())) {
					GeoElement[] ret =
					{ (GeoElement) kernel.getManager3D().Point3DIn(c.getLabel(), (Region) arg[0])};
					return ret;
				}
				
				throw argErr(app, "Point", arg[0]); 
			}


		default :

		}


		return super.process(c);
	}

}
