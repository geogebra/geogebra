package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;

/**
 * SetConditionToShowObject
 */
public class CmdSetConditionToShowObject extends CmdScripting {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSetConditionToShowObject(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected final GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement arg2[];
		switch (n) {
		case 2:
			arg2 = resArgs(c);
			if (arg2[1].isGeoBoolean()) {

				GeoElement geo = arg2[0];

				try {
					geo.setShowObjectCondition((GeoBoolean) arg2[1]);
				} catch (CircularDefinitionException e) {
					e.printStackTrace();
					throw argErr(app, c.getName(), arg2[1]);
				}
				geo.updateRepaint();

				return new GeoElement[0];
			}
			throw argErr(app, c.getName(), arg2[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
