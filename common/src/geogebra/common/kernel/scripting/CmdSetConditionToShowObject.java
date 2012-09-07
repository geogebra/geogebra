package geogebra.common.kernel.scripting;

import geogebra.common.kernel.CircularDefinitionException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CmdScripting;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 *SetConditionToShowObject
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
	protected
	final void perform(Command c) throws MyError {
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

				
				return;
			}
			throw argErr(app, c.getName(), arg2[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
