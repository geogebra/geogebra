package geogebra3D.kernel3D.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.Region;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdPointIn;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;


/*
 * PointIn[ <Region> ] 
 */
public class CmdPointIn3D extends CmdPointIn {

	
	
	public CmdPointIn3D(Kernel kernel) {
		super(kernel);
		
		
	}	
	

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		if (n==1) {
			arg = resArgs(c);
			if (ok[0] = (arg[0].isRegion())) {
				if (arg[0].isGeoElement3D()){
					GeoElement[] ret =
					{ (GeoElement) ((Kernel)kernelA).getManager3D().Point3DIn(c.getLabel(), (Region) arg[0], false)};
					return ret;
				}else
					return super.process(c);
			} else
				throw argErr(app, c.getName(), arg[0]);
		}else
			throw argNumErr(app, c.getName(), n);


	}
}