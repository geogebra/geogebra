package org.geogebra.common.kernel.scripting;

import org.geogebra.common.euclidian.EuclidianViewInterfaceSlim;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoAxisND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.EuclidianSettings;

/**
 * SetVisibleInView
 */
public class CmdSetVisibleInView extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetVisibleInView(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 3:
			GeoElement[] arg = resArgs(c);
			if (!(arg[1] instanceof NumberValue))
				throw argErr(app, c.getName(), arg[1]);

			if (arg[2].isGeoBoolean()) {

				GeoElement geo = arg[0];

				int viewNo = (int) ((NumberValue) arg[1]).getDouble();

				EuclidianViewInterfaceSlim ev = null;
				boolean show = ((GeoBoolean) arg[2]).getBoolean();

				int viewID;
				switch (viewNo) {
				case 1:
					viewID = App.VIEW_EUCLIDIAN;
					ev = app.getEuclidianView1();
					break;
				case 2:
					viewID = App.VIEW_EUCLIDIAN2;
					if (app.hasEuclidianView2(1)) {

						ev = app.getEuclidianView2(1);
					}
					break;
				case -1:
					viewID = App.VIEW_EUCLIDIAN3D;
					if (app.isEuclidianView3Dinited()) {
						ev = app.getEuclidianView3D();
					}
					break;
				default:
					return arg;
				}
				if (geo instanceof GeoAxisND) {

					EuclidianSettings evs = app.getSettings().getEuclidian(
							viewNo < 0 ? 3 : viewNo);

					evs.setShowAxis(((GeoAxisND) geo).getType(), show);
					geo.updateRepaint();
					return arg;
				}
				if (show) {
					geo.setEuclidianVisible(true);
					geo.addView(viewID);
					if (ev != null) {
						ev.add(geo);
						geo.updateRepaint();
					}
				} else {
					geo.removeView(viewID);
					if (ev != null) {
						ev.remove(geo);
						geo.updateRepaint();
					}
				}

				return arg;
			}
			throw argErr(app, c.getName(), arg[2]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
