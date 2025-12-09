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
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3D;
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog for rotation around a line
 */
public class InputDialogRotateAxisW extends InputDialogRotateW {

	private GeoLineND[] lines;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param selectedLines
	 *            selected lines
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotateAxisW(AppW app, DialogData data,
            NumberInputHandler handler, GeoPolygon[] polys,
            GeoLineND[] selectedLines, GeoElement[] selGeos,
            EuclidianController ec) {
		super(app, data, handler, polys, selGeos, ec);
		this.lines = selectedLines;
	}

	@Override
	protected void processInput(AsyncOperation<String> callback) {
		EuclidianController3D.rotateObject(app,
				getInputText(), isClockWise(), polys,
				lines, selGeos, (EuclidianController3D) ec, this, callback);
	}
}