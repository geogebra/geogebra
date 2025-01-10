package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * SetAxesRatio[x,y] SetAxesRatio[x,y,z] for 3D
 *
 */
public class CmdSetAxesRatio extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetAxesRatio(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok0;
		switch (n) {

		case 2:
			GeoElement[] arg = resArgs(c);

			if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoNumeric()) {

				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				EuclidianView ev = app.getActiveEuclidianView();
				if (ev.isDefault2D()) {
					ev.zoomAxesRatio(numGeo.getDouble(), numGeo2.getDouble(),
							true);
				} else if (ev.isEuclidianView3D()) {
					((EuclidianView3DInterface) ev).zoomAxesRatio(
							numGeo.getDouble() / numGeo2.getDouble(), 0);
				}

				return arg;

			}
			throw argErr(c, ok0 ? arg[1] : arg[0]);
		case 3:
			arg = resArgs(c);
			boolean ok1 = false;
			if ((ok0 = arg[0].isGeoNumeric()) && (ok1 = arg[1].isGeoNumeric())
					&& arg[2].isGeoNumeric()) {
				
				GeoNumeric numGeo = (GeoNumeric) arg[0];
				GeoNumeric numGeo2 = (GeoNumeric) arg[1];
				GeoNumeric numGeo3 = (GeoNumeric) arg[2];
				EuclidianView ev = app.getActiveEuclidianView();
				if (ev.isDefault2D()) {
					ev.zoomAxesRatio(numGeo.getDouble(), numGeo2.getDouble(),
							true);
				} else if (ev.isEuclidianView3D()) {
					((EuclidianView3DInterface) ev).zoomAxesRatio(
							numGeo.getDouble() / numGeo2.getDouble(),
							numGeo.getDouble() / numGeo3.getDouble());
				}

				return arg;

			}
			throw argErr(c, ok0 ? (ok1 ? arg[2] : arg[1]) : arg[0]);
		default:
			throw argNumErr(c);
		}
	}
}
