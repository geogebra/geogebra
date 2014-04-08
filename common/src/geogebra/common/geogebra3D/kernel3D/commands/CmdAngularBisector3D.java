package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdAngularBisector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;

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
	
	
	@Override
	protected GeoElement angularBisector(String label, GeoPointND A, GeoPointND B, GeoPointND C){

		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()){
			return kernelA.getManager3D().AngularBisector3D(label, A, B, C);
		}

		return super.angularBisector(label, A, B, C);
	}
	
}
