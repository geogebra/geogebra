package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdCircleArc;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;

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
}
