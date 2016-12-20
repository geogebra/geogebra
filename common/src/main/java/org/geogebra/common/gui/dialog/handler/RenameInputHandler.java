package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Construction.Constants;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class RenameInputHandler implements InputHandler {
	private GeoElementND geo;

	private boolean storeUndo;

	private App app;

	public RenameInputHandler(App app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	public void setGeoElement(GeoElementND geo) {
		this.geo = geo;
	}

	public void processInput(String inputValue, ErrorHandler handler,
			AsyncOperation<Boolean> callback) {
		GeoElementND geo = this.geo;

		if (inputValue == null) {
			callback.callback(false);
			return;
		}

		if (inputValue.equals(geo.getLabel(StringTemplate.defaultTemplate))) {
			callback.callback(true);
			return;
		}
		if (!LabelManager.checkName(geo, inputValue)) {
			app.showError("InvalidInput", inputValue);
			callback.callback(false);
			return;
		}

		try {
			Kernel kernel = app.getKernel();
			String newLabel = kernel.getAlgebraProcessor()
					.parseLabel(inputValue);

			newLabel = checkFreeLabel(kernel, newLabel);

			if (geo.rename(newLabel) && storeUndo) {
				app.storeUndoInfo();
			}

			callback.callback(true);
			return;
		} catch (Exception e) {
			app.showError("InvalidInput", inputValue);
		} catch (MyError err) {
			app.showError("InvalidInput", inputValue);
		}

		callback.callback(false);

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
