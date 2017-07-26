package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.GTimer;
import org.geogebra.common.util.GTimerListener;

/**
 * SelectObjects
 */
public class CmdSelectObjects extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSelectObjects(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		app.getSelectionManager().clearSelectedGeos(false);

		if (n > 0) {
			GeoElement[] arg = resArgs(c);
			for (int i = 0; i < n; i++) {
				if ((arg[i].isGeoElement())) {
					final GeoElement geo = arg[i];
					if (geo instanceof GeoInputBox) {
						deferredFocus((GeoInputBox) geo, 5);

					} else {
						app.getSelectionManager().addSelectedGeo(geo, false,
								false);
					}
				}
			}

			kernel.notifyRepaint();
			return arg;

		}
		app.getActiveEuclidianView().getEuclidianController().cancelDrag();

		kernel.notifyRepaint();
		app.updateSelection(false);
		return new GeoElement[0];
	}

	/**
	 * Keeps focus in an input box using repeated focus calls. TODO replace this
	 * by callback in EuclidianController
	 * 
	 * @param geo
	 *            input box
	 * @param reps
	 *            number of repetitions
	 */
	void deferredFocus(final GeoInputBox geo, final int reps) {
		final App app1 = app;
		app1.getActiveEuclidianView().focusAndShowTextField(geo);
		final GTimer timer = app.newTimer(new GTimerListener() {

			public void onRun() {
				if (reps > 0) {
					deferredFocus(geo, reps - 1);
				}

				app1.getActiveEuclidianView().refreshTextfieldFocus(geo);

			}
		}, 100);
		timer.start();

	}
}
