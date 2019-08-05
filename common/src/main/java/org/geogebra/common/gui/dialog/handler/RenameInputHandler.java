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
