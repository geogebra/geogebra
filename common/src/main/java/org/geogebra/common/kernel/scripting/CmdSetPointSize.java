package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.PointProperties;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.main.MyError;

/**
 * SetPointSize
 */
public class CmdSetPointSize extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetPointSize(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);

			boolean ok = false;
			if (arg[1] instanceof NumberValue) {
				ok = true;
				double size = ((NumberValue) arg[1]).getDouble();
				if (arg[0] instanceof PointProperties) {

					if (size > 0) {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(
								true);
						((PointProperties) arg[0]).setPointSize((int) size);
					} else {
						arg[0].setEuclidianVisibleIfNoConditionToShowObject(
								false);
					}
					arg[0].updateVisualStyleRepaint(GProperty.COMBINED);

					return arg;
				}

				if (arg[0] instanceof GeoPolyhedronInterface) {
					GeoPolyhedronInterface poly = (GeoPolyhedronInterface) arg[0];
					poly.setPointSizeOrVisibility((int) size);
					return arg;
				}

				if (arg[0].isGeoPolygon()) {
					GeoPolygon poly = (GeoPolygon) arg[0];
					poly.setPointSizeOrVisibility((int) size);
					return arg;
				}

				if (arg[0].isGeoNumeric()) {
					GeoNumeric poly = (GeoNumeric) arg[0];
					poly.setSliderBlobSize(size);
					poly.updateVisualStyleRepaint(GProperty.COMBINED);
					return arg;
				}
			}

			if (!ok) {
				throw argErr(app, c, arg[1]);
			}

			throw argErr(app, c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
