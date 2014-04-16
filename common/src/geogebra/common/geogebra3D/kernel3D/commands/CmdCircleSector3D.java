package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdCircleSector;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

public class CmdCircleSector3D extends CmdCircleSector {
	
	
	
	
	public CmdCircleSector3D(Kernel kernel) {
		super(kernel);
	}
	
	@Override
	protected GeoElement circleSector(String label, GeoPointND center, GeoPointND startPoint, GeoPointND endPoint){
		
		if (center.isGeoElement3D() || startPoint.isGeoElement3D() || endPoint.isGeoElement3D()){
			return (GeoElement) kernelA.getManager3D().CircleSector3D(label, center, startPoint, endPoint);
		}
		
		return super.circleSector(label, center, startPoint, endPoint);
	}
}
