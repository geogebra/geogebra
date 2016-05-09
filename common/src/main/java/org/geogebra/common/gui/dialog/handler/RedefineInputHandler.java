package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.util.AsyncOperation;

public class RedefineInputHandler implements InputHandler {

	private GeoElement geo;
	App app;
	String oldString;

	public RedefineInputHandler(App app, GeoElement geo, String oldString) {
		this.geo = geo;
		this.app = app;
		this.oldString = oldString;
	}
	
	/**
	 * @param geo
	 */
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
		oldString = geo.getRedefineString(false, true);
	}
	
	/**
	 * 
	 * @return current geo
	 */
	public GeoElement getGeoElement(){
		return geo;
	}

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
						+ ((FunctionalNVar) geo).getVarString(StringTemplate.defaultTemplate)
						+ ")=" + inputValue;
			}
			final String input = inputValue;
			app.getKernel().getAlgebraProcessor().changeGeoElement(geo,
					inputValue, true, true, handler,
					new AsyncOperation<GeoElement>() {

						@Override
						public void callback(GeoElement newGeo) {
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
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		callback.callback(false);
	}
}
