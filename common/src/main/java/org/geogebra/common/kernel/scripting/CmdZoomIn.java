package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * ZoomIn
 */
public class CmdZoomIn extends CmdScripting {
	/**
	 * Creates new ZooomOut command
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZoomIn(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianViewInterfaceSlim ev = app.getActiveEuclidianView();
				double px = ev.getWidth() / 2; // mouseLoc.x;
				double py = ev.getHeight() / 2; // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (Kernel.isZero(factor))
					throw argErr(app, c.getName(), arg[0]);

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				return arg;

			}
			throw argErr(app, c.getName(), arg[0]);
		case 2:
			arg = resArgs(c);
			return zoomIn2(arg, c.getName(), arg[0].evaluateDouble(), this);
		case 4:
			arg = resArgs(c);
			for (int i = 0; i < 3; i++) {
				if (!(arg[i] instanceof NumberValue)) {
					throw argErr(app, c.getName(), arg[i]);
				}
			}
			EuclidianSettings evs = app.getActiveEuclidianView().getSettings();
			evs.setXminObject((GeoNumeric) arg[0], false);
			evs.setXmaxObject((GeoNumeric) arg[2], false);
			evs.setYminObject((GeoNumeric) arg[1], false);
			evs.setYmaxObject((GeoNumeric) arg[3], true);
			app.getActiveEuclidianView().repaintView();
			return arg;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * @param arg
	 * @param cName
	 * @param factor
	 * @param proc
	 * @return
	 */
	protected static GeoElement[] zoomIn2(GeoElement[] arg, String cName,
			double factor, CmdScripting proc) {

		boolean ok0;
		if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {

			EuclidianViewInterfaceCommon ev = proc.getApp()
					.getActiveEuclidianView();
			Coords coords = ev.getCoordsForView(((GeoPointND) arg[1])
					.getCoordsInD3());
			double px = ev.toScreenCoordXd(coords.getX());
			double py = ev.toScreenCoordYd(coords.getY());

			if (!Kernel.isZero(factor)) {

				ev.zoom(px, py, factor, 4, true);

				proc.getApp().setUnsaved();
			}

			return arg;

		}
		throw proc.argErr(proc.getApp(), cName, ok0 ? arg[1] : arg[0]);
	}
}
