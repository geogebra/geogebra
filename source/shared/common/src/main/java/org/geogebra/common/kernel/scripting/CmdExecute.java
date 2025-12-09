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

import org.geogebra.common.kernel.CommandLookupStrategy;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.debug.Log;

/**
 * Execute[&lt;list of commands&gt;]
 */
public class CmdExecute extends CmdScripting {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdExecute(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] perform(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		if (n > 10 || n == 0) {
			throw argNumErr(c);
		}
		if (arg[0].isGeoList() && ((GeoList) arg[0]).size() == 0
				|| !arg[0].isDefined()) {
			return new GeoElement[] {};
		}
		if (!arg[0].isGeoList() || !arg[0]
				.getGeoElementForPropertiesDialog().isGeoText()) {
			throw argErr(c, arg[0]);
		}
		GeoList list = (GeoList) arg[0];

		// this is new in GeoGebra 4.2 and it will stop some files working
		// but causes problems if the files are opened and edited
		// and in the web project
		CommandLookupStrategy oldVal = kernel.getCommandLookupStrategy();
		kernel.setCommandLookupStrategy(CommandLookupStrategy.XML);

		for (int i = 0; i < list.size(); i++) {
			try {
				String cmdText = ((GeoText) list.get(i)).getTextStringSafe();
				for (int k = 1; k < n; k++) {
					cmdText = cmdText.replace("%" + k,
							arg[k].getLabel(StringTemplate.maxDecimals));
				}
				kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptionHandling(cmdText,
								false, app.getErrorHandler(), false, null);
			} catch (MyError e) {
				app.showError(e);
				break;
			} catch (Exception e) {
				app.showError(Errors.InvalidInput);
				Log.debug(e);
				break;
			}
		}
		kernel.setCommandLookupStrategy(oldVal);
		return arg;
	}
}
