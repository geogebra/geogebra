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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CmdScripting;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.properties.FillType;

public class CmdSetImage extends CmdScripting {

	/**
	 * Create new command processor
	 * @param kernel kernel
	 */
	public CmdSetImage(Kernel kernel) {
		super(kernel);
	}

	@Override
	protected GeoElement[] perform(Command c) {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c);

		switch (n) {
		case 2:
			if (arg[0].isFillable()) {
				if (arg[1] instanceof GeoImage) {
					String fileName = arg[1].getImageFileName();
					if (fileName != null && !fileName.isEmpty()) {
						arg[0].setFillType(FillType.IMAGE);
						arg[0].setFillImage(fileName);
						arg[0].updateVisualStyleRepaint(GProperty.HATCHING);
					}
				} else if (arg[1] instanceof GeoText) {
					app.getImageManager().setImageForFillable(kernel, (GeoText) arg[1], arg[0]);
				} else {
					throw argErr(c, arg[1]);
				}
			} else {
				throw argErr(c, arg[0]);
			}
			return arg;
		default:
			throw argNumErr(c);
		}
	}
}
