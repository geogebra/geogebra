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

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * ToolImage
 */
public class CmdToolImage extends CmdScripting {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdToolImage(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] perform(final Command c) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		arg = resArgs(c);

		final GeoPoint corner = checkarg(arg, c, 1);
		final GeoPoint corner2 = checkarg(arg, c, 2);

		switch (n) {

		case 2:
		case 3:
			// FALL THROUGH
		case 1:
			if (arg[0].isGeoNumeric()) {

				int mode = (int) ((GeoNumeric) arg[0]).getDouble();

				if (mode == -1) {
					int[] str = { 71, 101, 111, 71, 101, 98, 114, 97 };
					StringBuilder sb = new StringBuilder(str.length + 2);
					sb.append("\"");
					for (int i = 0 ; i < str.length ; i++) {
						sb.append((char) str[i]);
					}

					sb.append(" ");
					sb.append(kernel.getApplication().getVersionString());
					sb.append("\"");

					GeoText geo = kernel.getAlgebraProcessor().evaluateToText(
							sb.toString(),
							false, false);
					geo.setLabel(c.getLabel());
					return geo.asArray();
				}

				String modeStr = StringUtil
						.toLowerCaseUS(EuclidianConstants.getModeText(mode));

				if ("".equals(modeStr)) {
					throw argErr(c, arg[0]);
				}

				// TODO Fix me
				final Construction cons1 = app.getKernel().getConstruction();
				final GeoImage geoImage = new GeoImage(cons1);
				AsyncOperation<String> callback = fileName -> {
					geoImage.setImageFileName(fileName);
					geoImage.setTooltipMode(GeoElementND.TOOLTIP_OFF);

					try {
						geoImage.setStartPoint(corner == null
								? new GeoPoint(cons1, 0, 0, 1) : corner);

						if (corner2 != null) {
							geoImage.setCorner(corner2, 1);
						}

					} catch (CircularDefinitionException e) {
						Log.debug(e);
					}
					geoImage.setLabel(c.getLabel());

				};
				if (app.getGuiManager() != null) {
					app.getGuiManager().getToolImageURL(mode,
							geoImage, callback);
				} else {
					callback.callback("");
				}

				return new GeoElement[0];
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

	private GeoPoint checkarg(GeoElement[] arg, Command c, int i) {
		if (arg.length <= i) {
			return null;
		}
		if (arg[i] instanceof GeoPoint) {
			return (GeoPoint) arg[i];
		}
		throw argErr(c, arg[i]);
	}
}
