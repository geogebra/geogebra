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
import org.geogebra.common.gui.dialog.handler.SegmentHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog for segment with fixed radius.
 */
public class InputDialogSegmentFixedW extends ComponentInputDialog {
	private GeoPointND geoPoint1;
	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param point1
	 *            startpoint
	 * @param kernel
	 *            kernel
	 */
	public InputDialogSegmentFixedW(AppW app, DialogData data,
			InputHandler handler, GeoPointND point1, Kernel kernel) {
		super(app, data, false, false, handler, app.getLocalization().getMenu("Length"),
				"");
		this.kernel = kernel;
		geoPoint1 = point1;
	}

	@Override
	public void processInput() {
		new SegmentHandler(geoPoint1, kernel).doSegmentFixedAsync(
				getInputText(),
				(NumberInputHandler) getInputHandler(), this,
				ok -> {
					if (ok) {
						hide();
					}
				});
	}
}