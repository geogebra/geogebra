package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdAngularBisector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;

public class CmdAngularBisector3D extends CmdAngularBisector {
	
	
	
	
	public CmdAngularBisector3D(Kernel kernel) {
		super(kernel);
	}

	
	@Override
	protected GeoElement[] angularBisector(String[] labels, GeoLineND g, GeoLineND h){
		
		if (g.isGeoElement3D() || h.isGeoElement3D()){
			GeoElement[] ret = kernelA.getManager3D().AngularBisector3D(labels, g, h);
			return ret;
		}

		return super.angularBisector(labels, g, h);
	}
	
	
}
