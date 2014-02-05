package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdAngle;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class CmdAngle3D extends CmdAngle {
	
	
	
	
	public CmdAngle3D(Kernel kernel) {
		super(kernel);
	}

	
	


	@Override
	protected GeoElement[] angle(String label, GeoPointND p1, GeoPointND p2, GeoPointND p3){
		if (p1.isGeoElement3D() || p2.isGeoElement3D() || p3.isGeoElement3D()){
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, p1, p2, p3) };
			return ret;
		}

		return super.angle(label, p1, p2, p3);
	}
	
	@Override
	protected GeoElement[] angle(String label, GeoLineND g, GeoLineND h){
		
		if (g.isGeoElement3D() || h.isGeoElement3D()){
			GeoElement[] ret = { kernelA.getManager3D().Angle3D(label, g, h) };
			return ret;
		}

		return super.angle(label, g, h);
	}
}
