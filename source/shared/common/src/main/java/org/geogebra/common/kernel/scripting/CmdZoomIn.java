package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Inspecting;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.util.DoubleUtil;

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
		case 0:
			app.setStandardView();
			return new GeoElement[0];
		case 1:
			GeoElement[] arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				EuclidianViewInterfaceSlim ev = app.getActiveEuclidianView();
				double px = ev.getWidth() / 2.0; // mouseLoc.x;
				double py = ev.getHeight() / 2.0; // mouseLoc.y;

				double factor = numGeo.getDouble();
				if (DoubleUtil.isZero(factor)) {
					throw argErr(c, arg[0]);
				}

				ev.zoom(px, py, factor, 4, true);

				app.setUnsaved();

				return arg;

			}
			throw argErr(c, arg[0]);
		case 2:
			arg = resArgs(c);
			return zoomIn2(arg, c, arg[0].evaluateDouble(), this);
		case 4:
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if (!(arg[i] instanceof NumberValue)
						|| !Double.isFinite(arg[i].evaluateDouble())) {
					throw argErr(c, arg[i]);
				}
			}

			EuclidianView view = app.getActiveEuclidianView();
			EuclidianSettings evs = view.getSettings();

			// eg ZoomIn(a, a, -4, 4)
			evs.setXminObject((NumberValue) arg[0], false);
			evs.setYminObject((NumberValue) arg[1], false);
			evs.setXmaxObject((NumberValue) arg[2], false);
			evs.setYmaxObject((NumberValue) arg[3], true);
			view.repaintView();
			// don't return the args: don't need to delete them in case they are
			// dynamic
			return new GeoElement[0];

		case 6:
			arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if (!(arg[i] instanceof NumberValue)
						|| !Double.isFinite(arg[i].evaluateDouble())) {
					throw argErr(c, arg[i]);
				}
			}

			if (!app.isEuclidianView3Dinited()) {
				return new GeoElement[0];
			}

			EuclidianSettings3D evs3D =
					(EuclidianSettings3D) app.getEuclidianView3D().getSettings();
			if (evs3D != null) {
				evs3D.setUpdateScaleOrigin(false);
				for (int i = 0; i < n; i++) {
					if (Inspecting.isDynamicGeoElement(arg[i])) {
						evs3D.setUpdateScaleOrigin(true);
						break;
					}
				}

				// eg ZoomIn(a, a, a, 4, 4, 4)
				evs3D.setXminObject((GeoNumeric) arg[0], false);
				evs3D.setYminObject((GeoNumeric) arg[1], false);
				evs3D.setZminObject((GeoNumeric) arg[2], false);
				evs3D.setXmaxObject((GeoNumeric) arg[3], false);
				evs3D.setYmaxObject((GeoNumeric) arg[4], false);
				evs3D.setZmaxObject((GeoNumeric) arg[5], true);
			}

			double xMin = arg[0].evaluateDouble();
			double yMin = arg[1].evaluateDouble();
			double zMin = arg[2].evaluateDouble();
			double xMax = arg[3].evaluateDouble();
			double yMax = arg[4].evaluateDouble();
			double zMax = arg[5].evaluateDouble();

			if (xMin >= xMax) {
				throw argErr(c, arg[0]);
			}
			if (yMin >= yMax) {
				throw argErr(c, arg[1]);
			}
			if (zMin >= zMax) {
				throw argErr(c, arg[2]);
			}

			// eg ZoomIn(-5, 5, -5, 5, 5, 5)
			kernel.getApplication().getGgbApi().setCoordSystem(xMin, xMax, yMin, yMax, zMin, zMax,
					false);

			// don't return the args: don't need to delete them in case they are
			// dynamic
			return new GeoElement[0];

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param arg
	 *            arguments
	 * @param c
	 *            command
	 * @param factor
	 *            zoom factor
	 * @param proc
	 *            command processor
	 * @return args to delete
	 */
	protected static GeoElement[] zoomIn2(GeoElement[] arg, Command c,
			double factor, CmdScripting proc) {

		boolean ok0;
		if ((ok0 = arg[0].isGeoNumeric()) && arg[1].isGeoPoint()) {

			EuclidianViewInterfaceCommon ev = proc.getApp()
					.getActiveEuclidianView();
			Coords coords = ev
					.getCoordsForView(((GeoPointND) arg[1]).getCoordsInD3());
			double px = ev.toScreenCoordXd(coords.getX());
			double py = ev.toScreenCoordYd(coords.getY());

			if (!DoubleUtil.isZero(factor)) {

				ev.zoom(px, py, factor, 4, true);

				proc.getApp().setUnsaved();
			}

			return arg;

		}
		throw proc.argErr(c, ok0 ? arg[1] : arg[0]);
	}
}
