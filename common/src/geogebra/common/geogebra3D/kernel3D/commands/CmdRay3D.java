package geogebra.common.geogebra3D.kernel3D.commands;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoRayPointVector3D;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.commands.CmdRay;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;

/*
 * Ray[ <GeoPoint3D>, <GeoPoint3D> ] or CmdRay
 */
public class CmdRay3D extends CmdRay {

	public CmdRay3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return (GeoElement) kernelA.getManager3D().Ray3D(label, a, b);
		}

		return super.ray(label, a, b);
	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoVectorND v) {

		if (a.isGeoElement3D() || v.isGeoElement3D()) {
			AlgoRayPointVector3D algo = new AlgoRayPointVector3D(
					kernelA.getConstruction(), label, a, v);
			return algo.getLine();
		}

		return super.ray(label, a, v);
	}

}
