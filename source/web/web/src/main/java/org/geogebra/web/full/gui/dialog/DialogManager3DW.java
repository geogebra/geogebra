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
import org.geogebra.common.gui.dialog.handler.NumberInputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.web.full.gui.components.ComponentInputDialog;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;

/**
 * Dialog manager for 3D
 *
 */
public class DialogManager3DW extends DialogManagerW {
	/**
	 * @param app
	 *            application
	 */
	public DialogManager3DW(AppW app) {
		super(app);
	}

	/**
	 * 
	 * @param title
	 *            title
	 * @param center
	 *            sphere center
	 */
	@Override
	public void showNumberInputDialogSpherePointRadius(String title,
			GeoPointND center, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog spherePointInputDialog =
				new InputDialogSpherePointW((AppW) app, data,
				handler, center, app.getKernel());
		spherePointInputDialog.show();

	}

	/**
	 * for creating a cone
	 * 
	 * @param title
	 *            title
	 * @param a
	 *            basis center
	 * @param b
	 *            apex point
	 */
	@Override
	public void showNumberInputDialogConeTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog coneTwoPointsInputDialog
				= new InputDialogConeTwoPointsRadiusW((AppW) app, data,
				handler, a, b, app.getKernel());
		coneTwoPointsInputDialog.show();
	}

	@Override
	public void showNumberInputDialogCylinderTwoPointsRadius(String title,
			GeoPointND a, GeoPointND b, EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog cylinderTwoPointsInputDialog
				= new InputDialogCylinderTwoPointsRadiusW((AppW) app,
				data, handler, a, b, app.getKernel());
		cylinderTwoPointsInputDialog.show();
	}

	@Override
	public void showNumberInputDialogCirclePointDirectionRadius(String title,
			GeoPointND geoPoint, GeoDirectionND forAxis,
			EuclidianController ec) {
		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog circlePointRadiusInputDialog
				= new InputDialogCirclePointDirectionRadiusW((AppW) app,
				data, handler, geoPoint, forAxis, app.getKernel());
		circlePointRadiusInputDialog.show();
	}

	@Override
	public void showNumberInputDialogRotate(String title, GeoPolygon[] polys,
			GeoLineND[] selectedLines, GeoElement[] selGeos,
			EuclidianController ec) {

		NumberInputHandler handler = new NumberInputHandler(
				app.getKernel().getAlgebraProcessor());
		DialogData data = new DialogData(title);
		ComponentInputDialog rotateAxisInputDialog = new InputDialogRotateAxisW((AppW) app, data,
				handler, polys, selectedLines, selGeos, ec);
		rotateAxisInputDialog.show();

	}
}