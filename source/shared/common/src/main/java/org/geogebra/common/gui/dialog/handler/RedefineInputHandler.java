package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

/**
 * Input handler for redefine dialog
 */
public class RedefineInputHandler implements InputHandler {

	private GeoElementND geo;
	App app;
	String oldString;

	/**
	 * @param app
	 *            app
	 * @param geo
	 *            redefined element
	 * @param oldString
	 *            old definition
	 */
	public RedefineInputHandler(App app, GeoElement geo, String oldString) {
		this.geo = geo;
		this.app = app;
		this.oldString = oldString;
	}

	/**
	 * @param geo
	 *            redefined element
	 */
	public void setGeoElement(GeoElementND geo) {
		this.geo = geo;
		oldString = geo.getRedefineString(false, true);
	}

	/**
	 * 
	 * @return current geo
	 */
	public GeoElementND getGeoElement() {
		return geo;
	}

	@Override
	public void processInput(String rawInput, ErrorHandler handler,
			final AsyncOperation<Boolean> callback) {
		if (rawInput == null) {
			callback.callback(false);
			return;
		}
		if (rawInput.equals(this.oldString)) {
			callback.callback(true);
			return;

		}
		try {
			String inputValue = rawInput;
			if (geo instanceof FunctionalNVar) {
				// string like f(x,y)=x^2
				// or f(\theta) = \theta
				inputValue = geo.getLabel(StringTemplate.defaultTemplate) + "("
						+ ((FunctionalNVar) geo)
								.getVarString(StringTemplate.defaultTemplate)
						+ ")=" + inputValue;
			}
			final String input = inputValue;
			app.getKernel().getAlgebraProcessor().changeGeoElement(geo,
					inputValue, true, true, handler,
					new AsyncOperation<GeoElementND>() {

						@Override
						public void callback(GeoElementND newGeo) {
							app.getKernel().clearJustCreatedGeosInViews();

							if (newGeo != null) {
								app.doAfterRedefine(newGeo);

								// update after redefine
								// http://code.google.com/p/geogebra/issues/detail?id=147
								setGeoElement(newGeo);
								oldString = input;
								// -----------------------------------------------------------
							}

							// needed for Apply button
							if (callback != null) {
								callback.callback(newGeo != null);
							}
						}
					});

			return;
		} catch (Exception e) {
			app.showError(Errors.ReplaceFailed);
		} catch (MyError err) {
			app.showError(err);
		}
		callback.callback(false);
	}
}
