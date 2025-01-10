package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.ScreenReader;

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
				if (arg[i].isGeoElement()) {
					final GeoElement geo = arg[i];
					if (geo instanceof GeoInputBox) {
						deferredFocus((GeoInputBox) geo);

					} else {
						app.getSelectionManager().addSelectedGeo(geo, false,
								false);
						ScreenReader.readText(geo);
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
	 */
	void deferredFocus(final GeoInputBox geo) {
		final App app1 = app;
		final long expiration = System.currentTimeMillis() + 1000;
		Runnable callback = () -> {
			if (System.currentTimeMillis() < expiration) {
				app1.getActiveEuclidianView().focusAndShowTextField(geo);
			}
		};
		callback.run();
		app1.getActiveEuclidianView().getEuclidianController()
				.addPointerUpCallback(callback);

	}
}
