/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.kernel.scripting;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;

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
		GeoElement[] arg2;
		switch (n) {
		case 2:
			arg2 = resArgs(c);
			if (arg2[1].isGeoBoolean()) {

				GeoElement geo = arg2[0];

				try {
					geo.setShowObjectCondition((GeoBoolean) arg2[1]);
				} catch (CircularDefinitionException e) {
					Log.debug(e);
					throw argErr(c.getName(), arg2[1], e);
				}
				geo.updateRepaint();

				return new GeoElement[0];
			}
			throw argErr(c, arg2[1]);

		default:
			throw argNumErr(c);
		}
	}
}
