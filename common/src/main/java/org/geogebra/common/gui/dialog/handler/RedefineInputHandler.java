package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

public class RedefineInputHandler implements InputHandler {

	private GeoElement geo;
	private App app;
	private String oldString; // Michael Borcherds 2007-12-31

	public RedefineInputHandler(App app, GeoElement geo, String oldString) {
		this.geo = geo;
		this.app = app;
		this.oldString=oldString; // Michael Borcherds 2007-12-31
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

	public boolean processInput(String rawInput) {			
		if (rawInput == null)
			return false;
		if (rawInput.equals(this.oldString)) return true; // Michael Borcherds 2007-12-31
		try {
			String inputValue = rawInput;
			if (geo instanceof FunctionalNVar) {
				// string like f(x,y)=x^2
				// or f(\theta) = \theta
				inputValue = geo.getLabel(StringTemplate.defaultTemplate) + "("
						+ ((FunctionalNVar) geo).getVarString(StringTemplate.defaultTemplate)
						+ ")=" + inputValue;
			}
			GeoElement newGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(
					geo, inputValue, true, true);
			app.getKernel().clearJustCreatedGeosInViews();
			
            if (newGeo != null) {
            	app.doAfterRedefine(newGeo);
            
            	// update after redefine
            	// http://code.google.com/p/geogebra/issues/detail?id=147
            	setGeoElement(newGeo);
                oldString = inputValue;
                // -----------------------------------------------------------
            }

            
			return newGeo != null;
		} catch (Exception e) {
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		return false;
	}
}
