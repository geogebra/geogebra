package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdTangent;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoPointND;

public class CmdTangent3D extends CmdTangent {
	
	
	
	
	public CmdTangent3D(Kernel kernel) {
		super(kernel);
	}

	
	@Override
	protected GeoElement[] tangent(String[] labels, GeoPointND a, GeoConicND c){
		return kernelA.getManager3D().Tangent3D(labels, a, c);
	}
	
}
