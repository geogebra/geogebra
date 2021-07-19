package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * ReadText(Text)
 * 
 * @author Zbynek
 *
 */
public class CmdReadText extends CmdScripting {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdReadText(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {
		if (c.getArgumentNumber() != 1) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c);
		if (args[0].isGeoText()) {

			if (app.getActiveEuclidianView() != null) {
				app.getActiveEuclidianView().getScreenReader()
						.readDelayed(((GeoText) args[0]).getAuralText());
			}
			return args;
		}
		throw argErr(c, args[0]);
	}

}
