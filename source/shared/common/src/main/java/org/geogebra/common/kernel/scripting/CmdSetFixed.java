package org.geogebra.common.kernel.scripting;

import org.geogebra.common.gui.dialog.options.model.SelectionAllowedModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetFixed
 */
public class CmdSetFixed extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetFixed(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);
		GeoElement arg2 = null;

		switch (n) {
		case 3:
			arg2 = arg[2];

			if (!arg2.isGeoBoolean()) {
				throw argErr(c, arg2);
			}
		case 2:
			if (arg[1].isGeoBoolean()) {

				GeoElement geo = arg[0];
				geo.setFixed(((GeoBoolean) arg[1]).getBoolean());
				if (arg2 instanceof GeoBoolean) {
					boolean allowSelection = ((GeoBoolean) arg2).getBoolean();
					SelectionAllowedModel.applyTo(geo, app, allowSelection);
				} else {
					geo.updateVisualStyleRepaint(GProperty.COMBINED);
				}
				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
