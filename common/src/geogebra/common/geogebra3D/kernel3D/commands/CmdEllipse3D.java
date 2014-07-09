package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdEllipse;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdEllipse3D extends CmdEllipse {
	
	
	
	
	public CmdEllipse3D(Kernel kernel) {
		super(kernel);
	}

	
	@Override
	protected GeoElement ellipse(String label, GeoPointND a, GeoPointND b, GeoPointND c){
		
		if (a.isGeoElement3D() || b.isGeoElement3D() || c.isGeoElement3D()){
			return kernelA.getManager3D().Ellipse3D(label, a, b, c);
		}
		
		return super.ellipse(label, a, b, c);
	}

	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok) throws MyError{

		if ((ok[0] = (arg[0].isGeoPoint())) &&
				(ok[1] = (arg[1].isGeoPoint())) &&
				(ok[2] = (arg[2].isGeoPoint())) &&
				(ok[3] = (arg[3] instanceof GeoDirectionND))) {
			
			
			GeoElement[] ret = { kernelA.getManager3D().
					Ellipse3D(c.getLabel(), 
							(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoPointND) arg[2], 
							(GeoDirectionND) arg[3])};
			return ret;
		}
		
		return null;
	}
}
