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

package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Construction.Constants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Handler for rename dialog.
 */
public class RenameInputHandler implements InputHandler {
	private GeoElementND geo;

	private boolean storeUndo;

	private App app;

	/**
	 * @param app
	 *            application
	 * @param geo
	 *            renamed geo
	 * @param storeUndo
	 *            whether to store undo point after rename
	 */
	public RenameInputHandler(App app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	/**
	 * @param geo
	 *            renamed geo
	 */
	public void setGeoElement(GeoElementND geo) {
		this.geo = geo;
	}

	@Override
	public void processInput(String inputValue, ErrorHandler handler,
			AsyncOperation<Boolean> callback) {
		GeoElementND geo1 = this.geo;

		if (inputValue == null) {
			callback.callback(false);
			return;
		}

		if (inputValue.equals(geo1.getLabel(StringTemplate.defaultTemplate))) {
			callback.callback(true);
			return;
		}

		Kernel kernel = app.getKernel();
		String newLabel = inputValue;

		if (!LabelManager.isValidLabel(newLabel, kernel, (GeoElement) geo1)) {
			app.showError(Errors.InvalidInput, inputValue);
			callback.callback(false);
			return;
		}

		newLabel = checkFreeLabel(kernel, newLabel);

		if (geo1.rename(newLabel) && storeUndo) {
			app.storeUndoInfo();
		}

		callback.callback(true);

	}

	/**
	 * @param kernel
	 *            kernel
	 * @param newLabel
	 *            intended label
	 * @return alternate label if intended one is reserved
	 */
	public static String checkFreeLabel(Kernel kernel, String newLabel) {
		// is there a geo with this name?
		GeoElement existingGeo = kernel.lookupLabel(newLabel);

		if (existingGeo != null) {
			// rename this geo too:
			if (kernel.getConstruction()
					.isConstantElement(existingGeo) == Constants.NOT) {
				String tempLabel = existingGeo.getIndexLabel(newLabel);
				existingGeo.rename(tempLabel);
			} else {
				return existingGeo.getIndexLabel(newLabel);
			}
		}
		return newLabel;
	}

}
