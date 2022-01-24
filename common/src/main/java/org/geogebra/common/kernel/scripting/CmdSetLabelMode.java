package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * SetLabelMode
 */
public class CmdSetLabelMode extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetLabelMode(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 2:
			GeoElement[] arg = resArgs(c);
			if (arg[1].isGeoNumeric()) {

				GeoElement geo = arg[0];

				geo.setLabelMode((int) ((GeoNumeric) arg[1]).getDouble());
				geo.updateVisualStyleRepaint(GProperty.LABEL_STYLE);

				return arg;
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
