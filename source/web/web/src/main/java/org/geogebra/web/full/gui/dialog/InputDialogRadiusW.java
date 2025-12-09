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
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Circle or sphere dialog
 *
 */
public abstract class InputDialogRadiusW extends ComponentInputDialog {

	/** current kernel */
	protected Kernel kernel;

	/**
	 * 
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param kernel
	 *            kernel
	 */
	public InputDialogRadiusW(AppW app, DialogData data, InputHandler handler,
			Kernel kernel) {
		super(app, data, false, false, handler, "Radius",
				"");
		this.kernel = kernel;
	}

	@Override
	protected void toolAction() {
		GeoElement circle = createOutput(getNumber());
		GeoElement[] geos = { circle };
		app.storeUndoInfoAndStateForModeStarting();
		kernel.getApplication().getActiveEuclidianView()
				.getEuclidianController().memorizeJustCreatedGeos(geos);
	}

	/**
	 * @return input as number
	 */
	protected GeoNumberValue getNumber() {
		return ((NumberInputHandler) getInputHandler()).getNum();
	}

	/**
	 * 
	 * @param num
	 *            radius value
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(GeoNumberValue num);
}