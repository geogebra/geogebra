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

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog for dilate tool
 */
public class InputDialogDilateW extends ComponentInputDialog {
	private GeoPointND[] points;
	private GeoElement[] selGeos;
	private Kernel kernel;
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog trans keys
	 * @param handler
	 *            value handler
	 * @param points
	 *            input points
	 * @param selGeos
	 *            transformed geos
	 * @param kernel
	 *            kernel
	 * @param ec
	 *            euclidian controller
	 */
	public InputDialogDilateW(AppW app, DialogData data, InputHandler handler,
			GeoPointND[] points, GeoElement[] selGeos, Kernel kernel, EuclidianController ec) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu("Dilate.Factor"), ""
		);
		setInputHandler(handler);
		addStyleName("dilate");

		this.points = points;
		this.selGeos = selGeos;
		this.kernel = kernel;
		this.ec = ec;
	}

	/**
	 * Execute the dilation with input.
	 */
	@Override
	protected void toolAction() {
		DialogManager.doDilate(kernel,
				((NumberInputHandler) getInputHandler()).getNum(), points,
				selGeos, ec);
	}
}