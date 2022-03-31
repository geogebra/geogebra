package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoAxis;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.MyError;

/**
 * SetCaption
 */
public class CmdSetCaption extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetCaption(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isGeoText()) {

				GeoElement geo = arg[0];
				String txt = ((GeoText) arg[1]).getTextString();
				if (geo instanceof GeoAxis) {
					app.getActiveEuclidianView().getSettings()
							.setAxisLabel(((GeoAxis) geo).getType(), txt);
					app.getActiveEuclidianView().repaintView();
				} else {
					geo.setCaption(txt);
					geo.setLabelMode(GeoElementND.LABEL_CAPTION);
					geo.updateVisualStyleRepaint(GProperty.LABEL_STYLE);
				}
				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
