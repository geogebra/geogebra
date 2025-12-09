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

package org.geogebra.desktop.geogebra3D.gui.dialogs;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.desktop.gui.dialog.InputDialogRadiusD;
import org.geogebra.desktop.main.AppD;

/**
 * 
 *
 */
public class InputDialogSpherePointRadius extends InputDialogRadiusD {

	private GeoPointND geoPoint;

	/**
	 * @param app application
	 * @param title title
	 * @param handler input handler
	 * @param point center
	 * @param kernel kernel
	 */
	public InputDialogSpherePointRadius(AppD app, String title,
			InputHandler handler, GeoPointND point, Kernel kernel) {
		super(app, title, handler, kernel);

		geoPoint = point;

	}

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().sphere(null, geoPoint, num);
	}

}
