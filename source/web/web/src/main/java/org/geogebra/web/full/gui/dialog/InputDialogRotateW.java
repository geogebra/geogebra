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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.editor.share.util.Unicode;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Generic rotate dialog
 */
public abstract class InputDialogRotateW extends AngleInputDialogW {
	/** selected polygons */
	GeoPolygon[] polys;
	/** selected geos */
	GeoElement[] selGeos;
	/** controller */
	protected EuclidianController ec;
	/** 45 degrees */
	final protected static String DEFAULT_ROTATE_ANGLE = Unicode.FORTY_FIVE_DEGREES_STRING;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog data
	 * @param handler
	 *            input handler
	 * @param polys
	 *            selected polygons
	 * @param selGeos
	 *            selected geos
	 * @param ec
	 *            controller
	 */
	public InputDialogRotateW(AppW app, DialogData data,
			InputHandler handler, GeoPolygon[] polys,
			GeoElement[] selGeos, EuclidianController ec) {
		super(app, app.getLocalization().getMenu("Angle"), data,
				DEFAULT_ROTATE_ANGLE, handler, false);
		this.polys = polys;
		this.selGeos = selGeos;
		this.ec = ec;
	}

	@Override
	public void processInput() {
		processInput(obj -> {
			if (obj != null) {
				hide();
			}
		});
	}

	/**
	 * @param op
	 *            callback
	 */
	protected abstract void processInput(AsyncOperation<String> op);
}