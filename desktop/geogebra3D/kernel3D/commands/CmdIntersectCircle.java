package geogebra3D.kernel3D.commands;



import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoQuadricND;
import geogebra.common.main.MyError;

public class CmdIntersectCircle extends CommandProcessor {

	public CmdIntersectCircle(Kernel kernel) {
		super(kernel);
	}

	public  GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			// between 2 quadrics
			if ((ok[0] = (arg[0] instanceof GeoQuadricND))
					&& (ok[1] = (arg[1] instanceof GeoQuadricND))) {
				GeoElement[] ret =
						kernelA.getManager3D().IntersectAsCircle(
								c.getLabels(),
								(GeoQuadricND) arg[0],
								(GeoQuadricND) arg[1]); 
				return ret;
			} 

			
			throw argErr(app, c.getName(), getBadArg(ok,arg));
			

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}