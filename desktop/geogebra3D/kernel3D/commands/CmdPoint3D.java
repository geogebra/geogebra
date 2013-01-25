package geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Path;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;



/*
 * Point[ <Path (3D)> ] or Point[ <Region (3D)> ] or CmdPoint
 */
public class CmdPoint3D extends CmdPoint {
	
	public CmdPoint3D(Kernel kernel) {
		super(kernel);
		
		
	}
	
	
	@Override
	public GeoElement[] process(Command c) throws MyError {	


		int n = c.getArgumentNumber();
		GeoElement[] arg;



		switch (n) {
		case 1 :
			arg = resArgs(c);
			GeoElement geo0 = arg[0];

			if (geo0.isGeoElement3D() || (geo0.isGeoList() && ((GeoList) geo0).containsGeoElement3D()) ){
				if (geo0.isPath()) {
					GeoElement[] ret =
					{ (GeoElement) kernelA.getManager3D().Point3D(c.getLabel(), (Path) geo0, false)};
					return ret;
				}
				// if arg[0] isn't a Path, try to process it as a region (e.g. GeoPlane3D)
				if (geo0.isRegion()) {
					GeoElement[] ret =
					{ (GeoElement) kernelA.getManager3D().Point3DIn(c.getLabel(), (Region) arg[0], false)};
					return ret;
				}
				
				throw argErr(app, c.getName(), geo0); 
			}


		}

		return super.process(c);
		
	}
	

}
