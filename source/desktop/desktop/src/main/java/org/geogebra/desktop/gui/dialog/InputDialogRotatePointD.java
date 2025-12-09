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

package org.geogebra.desktop.gui.dialog;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.desktop.main.AppD;

public class InputDialogRotatePointD extends InputDialogRotateD {

	GeoPointND[] points;

	/**
	 * @param app application
	 * @param title title
	 * @param handler input handler
	 * @param polys selected polygons
	 * @param points selected points
	 * @param selGeos selected geos
	 * @param ec controller
	 */
	public InputDialogRotatePointD(AppD app, String title, InputHandler handler,
			GeoPolygon[] polys, GeoPointND[] points, GeoElement[] selGeos,
			EuclidianController ec) {

		super(app, title, handler, polys, selGeos, ec);

		this.points = points;
	}

	@Override
	protected void processInput(AsyncOperation<String> callback) {

		DialogManager.rotateObject(app, inputPanel.getText(),
				rbClockWise.isSelected(), polys,
				new DialogManager.CreateGeoForRotatePoint(points[0]), selGeos,
				ec, this, callback);

	}

}
