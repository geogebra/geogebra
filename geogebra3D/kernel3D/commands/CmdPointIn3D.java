package geogebra3D.kernel3D.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.Region;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.commands.CmdPointIn;
import geogebra.main.MyError;
import geogebra3D.kernel3D.Kernel3D;


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
					{ (GeoElement) kernel.getManager3D().Point3DIn(c.getLabel(), (Region) arg[0])};
					return ret;
				}else
					return super.process(c);
			} else
				throw argErr(app, "Point", arg[0]);
		}else
			throw argNumErr(app, "Point", n);


	}
}