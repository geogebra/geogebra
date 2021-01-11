package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoRayPointVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CmdRay;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Ray[ &lt;GeoPoint3D>, &lt;GeoPoint3D> ] or CmdRay
 */
public class CmdRay3D extends CmdRay {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdRay3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoPointND b) {
		if (a.isGeoElement3D() || b.isGeoElement3D()) {
			return (GeoElement) kernel.getManager3D().ray3D(label, a, b);
		}

		return super.ray(label, a, b);
	}

	@Override
	protected GeoElement ray(String label, GeoPointND a, GeoVectorND v) {

		if (a.isGeoElement3D() || v.isGeoElement3D()) {
			AlgoRayPointVector3D algo = new AlgoRayPointVector3D(
					kernel.getConstruction(),  a, v);
			algo.getLine().setLabel(label);
			return algo.getLine();
		}

		return super.ray(label, a, v);
	}

}
