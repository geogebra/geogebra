package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 *SetActiveView
 */
class CmdSetActiveView extends CmdScripting {

	public CmdSetActiveView(Kernel kernel) {
		super(kernel);
	}

	final public void perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		
		
		

		if (!app.isUsingFullGui()) return;
			
		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoNumeric()) {
				GeoNumeric numGeo = (GeoNumeric) arg[0];

				int view = (int)numGeo.getDouble();
				
				// ignore all errors (eg when a view is not available etc)
				switch (view) {
				case 1:
					app.setActiveView(AbstractApplication.VIEW_EUCLIDIAN);
					 break;
				case 2:
					app.setActiveView(AbstractApplication.VIEW_EUCLIDIAN2);
					 break;
				case 3:
					app.setActiveView(AbstractApplication.VIEW_EUCLIDIAN3D);
					 break;
				case -1:
					app.setActiveView(AbstractApplication.VIEW_SPREADSHEET);
					 break;
				case -2:
					app.setActiveView(AbstractApplication.VIEW_ALGEBRA);
					 break;
				case -3:
					app.setActiveView(AbstractApplication.VIEW_CAS);
					 break;
				// default: // might be needed when support for more than 2 Euclidian Views added 
				}
				
				return;

			} else
				throw argErr(app, c.getName(), arg[0]);


		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
