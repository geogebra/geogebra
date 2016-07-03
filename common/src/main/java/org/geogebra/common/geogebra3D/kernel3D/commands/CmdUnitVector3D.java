package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoUnitVector3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoUnitVector;
import org.geogebra.common.kernel.algos.CmdUnitVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * UnitOrthogonalVector[ <GeoPlane3D> ]
 */
public class CmdUnitVector3D extends CmdUnitVector {

	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdUnitVector3D(Kernel kernel, boolean normalize) {
		super(kernel, normalize);
	}

	@Override
	protected GeoElement[] processNotLineNotVector(Command c, GeoElement arg)
			throws MyError {

		if (arg instanceof GeoDirectionND) {
			AlgoUnitVector3D algo = new AlgoUnitVector3D(cons,
					(GeoDirectionND) arg, normalize);
			algo.getVector().setLabel(c.getLabel());
			GeoElement[] ret = { (GeoElement) algo.getVector() };
			return ret;
		}

		return super.processNotLineNotVector(c, arg);
	}

	@Override
	protected AlgoUnitVector algo(GeoLineND line) {

		if (line.isGeoElement3D()) {
			return new AlgoUnitVector3D(cons, line, normalize);
		}

		return super.algo(line);
	}

	@Override
	protected AlgoUnitVector algo(GeoVectorND v) {

		if (v.isGeoElement3D()) {
			return new AlgoUnitVector3D(cons, v, normalize);
		}

		return super.algo(v);
	}

}
