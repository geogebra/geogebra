package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdLength;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class CmdLength3D extends CmdLength {
	
	
	
	
	public CmdLength3D(Kernel kernel) {
		super(kernel);
	}

	
	@Override
	protected GeoElement length(String label, GeoVectorND v){
		if (v.isGeoElement3D()){
			return kernelA.getManager3D().Length(label, v);
		}

		return super.length(label, v);
	}
	
}
