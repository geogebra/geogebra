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
import org.geogebra.common.kernel.kernelND.GeoCoordSys2D;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Web dialog for regular polygons
 */
public class InputDialogRegularPolygonW extends ComponentInputDialog {
	private GeoPointND geoPoint1;
	private GeoPointND geoPoint2;
	private GeoCoordSys2D direction;
	private EuclidianController ec;

	/**
	 * @param app
	 *            application
	 * @param data
	 *            dialog translation keys
	 * @param ec
	 *            controller
	 * @param handler
	 *            input handler
	 * @param point1
	 *            first vertex
	 * @param point2
	 *            second vertex
	 * @param direction
	 *            orientation
	 */
	public InputDialogRegularPolygonW(AppW app, DialogData data, EuclidianController ec,
			InputHandler handler, GeoPointND point1, GeoPointND point2,
			GeoCoordSys2D direction) {
		super(app, data, false, false, handler,
				app.getLocalization().getMenu("Points"), "4"
		);
		geoPoint1 = point1;
		geoPoint2 = point2;
		this.direction = direction;
		this.ec = ec;
	}

	@Override
	public void processInput() {
		DialogManager.makeRegularPolygon(app, ec, getInputText(),
				geoPoint1, geoPoint2, direction, this,
				ok -> {
					if (ok) {
						hide();
					}
				});
	}
}