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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;

/**
 * Checkbox
 */
public class CmdCheckbox extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCheckbox(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		// dummy

		String caption = null;
		GeoList geosToHide = null;
		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if (arg[0].isGeoText()) {
				caption = ((GeoText) arg[0]).getTextString();
			} else {
				throw argErr(c, arg[0]);
			}
			if (arg[1].isGeoList()) {
				geosToHide = (GeoList) arg[1];
			} else {
				throw argErr(c, arg[1]);
			}
			break;
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoText()) {
				caption = ((GeoText) arg[0]).getTextString();
			} else if (arg[0].isGeoList()) {
				geosToHide = (GeoList) arg[0];
			} else {
				throw argErr(c, arg[0]);
			}
			break;
		case 0:
			break;

		default:
			throw argNumErr(c);
		}

		String label = c.getLabel();

		GeoElement geo = cons.lookupLabel(label);

		if (geo == null) {
			geo = new GeoBoolean(app.getKernel().getConstruction());
			((GeoBoolean) geo).setValue(true);
			geo.setEuclidianVisible(true);
			geo.setLabel(c.getLabel());
		}

		if (!geo.isGeoBoolean()) {
			// invalid input
			app.showError(Errors.InvalidInput,
					label + " = " + c.toString(StringTemplate.defaultTemplate));
			return new GeoElement[] { null };
		}

		GeoBoolean gb = (GeoBoolean) geo;

		if (caption != null) {
			gb.setLabelVisible(true);
			gb.setCaption(caption);
			gb.update();
		}
		try {

			if (geosToHide != null) {
				for (int i = 0; i < geosToHide.size(); i++) {
					geosToHide.get(i).setShowObjectCondition(gb);
				}
			}
		} catch (CircularDefinitionException e) {
			app.showError(Errors.CircularDefinition);
		}
		return new GeoElement[] { gb };
	}
}
