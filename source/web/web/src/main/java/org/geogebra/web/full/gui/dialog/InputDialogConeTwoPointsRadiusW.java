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

package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog for cone given by two points and radius
 *
 */
public class InputDialogConeTwoPointsRadiusW extends InputDialogRadiusW {

	private GeoPointND a;
	private GeoPointND b;

	/**
	 * 
	 * @param app
	 *            app
	 * @param data
	 *           dialog data
	 * @param handler
	 *            input handler
	 * @param a
	 *            bottom point
	 * @param b
	 *            vertex
	 * @param kernel
	 *            kernel
	 */
	public InputDialogConeTwoPointsRadiusW(AppW app, DialogData data,
			InputHandler handler, GeoPointND a, GeoPointND b, Kernel kernel) {
		super(app, data, handler, kernel);
		this.a = a;
		this.b = b;
	}

	@Override
	protected GeoElement createOutput(GeoNumberValue num) {
		return kernel.getManager3D().coneLimited(null, a, b, num)[0];
	}

}
