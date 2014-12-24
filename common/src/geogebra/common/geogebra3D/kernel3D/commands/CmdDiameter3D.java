package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdDiameter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoConicND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoVectorND;

public class CmdDiameter3D extends CmdDiameter {

	public CmdDiameter3D(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement diameter(String label, GeoLineND l, GeoConicND c) {

		return kernelA.getManager3D().DiameterLine3D(label, l, c);
	}

	@Override
	protected GeoElement diameter(String label, GeoVectorND v, GeoConicND c) {
		return kernelA.getManager3D().DiameterLine3D(label, v, c);
	}
}
