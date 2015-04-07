package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

/**
 * SetActiveView
 */
public class CmdSetActiveView extends CmdScripting {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetActiveView(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		if (!app.isUsingFullGui())
			return;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				int view = (int) numGeo.getDouble();

				// ignore all errors (eg when a view is not available etc)
				switch (view) {
				case 1:
					app.setActiveView(App.VIEW_EUCLIDIAN);
					break;
				case 2:
					app.setActiveView(App.VIEW_EUCLIDIAN2);
					break;
				case -1:
					app.setActiveView(App.VIEW_EUCLIDIAN3D);
					break;
				// default: // might be needed when support for more than 2
				// Euclidian Views added
				}

				return;

			} else if (arg[0].isGeoText()) {
				String code = arg[0]
						.toValueString(StringTemplate.defaultTemplate);
				if (code.length() == 1) {
					char letter = code.charAt(0);
					switch (letter) {
					case 'G':
						app.setActiveView(App.VIEW_EUCLIDIAN);
						break;
					case 'D':
						app.setActiveView(App.VIEW_EUCLIDIAN2);
						break;
					case 'T':
						app.setActiveView(App.VIEW_EUCLIDIAN3D);
						break;
					case 'S':
						app.setActiveView(App.VIEW_SPREADSHEET);
						break;
					case 'A':
						app.setActiveView(App.VIEW_ALGEBRA);
						break;
					case 'C':
						app.setActiveView(App.VIEW_CAS);
						break;

					}
				}

				return;
			}
			throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
