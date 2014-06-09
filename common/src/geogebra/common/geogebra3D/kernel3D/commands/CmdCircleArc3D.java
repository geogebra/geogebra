package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdCircleArc;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.MyError;

public class CmdCircleArc3D extends CmdCircleArc {
	
	
	
	
	public CmdCircleArc3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoElement circleArc(String label, GeoPointND center, GeoPointND startPoint, GeoPointND endPoint){
		
		if (center.isGeoElement3D() || startPoint.isGeoElement3D() || endPoint.isGeoElement3D()){
			return (GeoElement) kernelA.getManager3D().CircleArc3D(label, center, startPoint, endPoint);
		}
		
		return super.circleArc(label, center, startPoint, endPoint);
	}
	
	@Override
	protected GeoElement[] process4(Command c, GeoElement[] arg, boolean[] ok) throws MyError{

		// arc center-two points, oriented
		if ((ok[0] = (arg[0].isGeoPoint()))
				&& (ok[1] = (arg[1].isGeoPoint()))
				&& (ok[2] = (arg[2].isGeoPoint()))
				&& (ok[3] = (arg[3] instanceof GeoDirectionND))) {
			
			if (!arg[0].isGeoElement3D() 
					&& !arg[1].isGeoElement3D() 
					&& !arg[2].isGeoElement3D() 
					&& arg[3] == kernelA.getXOYPlane()){ // ignore xOy plane for 2D
				return new GeoElement[] {
						super.circleArc(c.getLabel(), 
								(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoPointND) arg[2])};
			}
			
			GeoElement[] ret = { (GeoElement) kernelA.getManager3D().
					CircleArc3D(c.getLabel(), 
							(GeoPointND) arg[0], (GeoPointND) arg[1], (GeoPointND) arg[2], 
							(GeoDirectionND) arg[3]) };
			return ret;
		}
		
		return null;
	}
}
