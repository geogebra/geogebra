package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdCircumcircleArc;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;




public class CmdCircumcircleArc3D extends CmdCircumcircleArc {
	

	public CmdCircumcircleArc3D(Kernel kernel) {
		super(kernel);
		
	}
	
	@Override
	protected GeoElement getArc(String label, GeoElement A, GeoElement B, GeoElement C){
		
		if (A.isGeoElement3D() || B.isGeoElement3D() || C.isGeoElement3D()){
			return (GeoElement) kernelA.getManager3D().CircumcircleArc3D(label, (GeoPointND) A,
					(GeoPointND) B, (GeoPointND) C);
		}
		
		return super.getArc(label, A, B, C);
	}
	
	
	
}
