package geogebra.common.gui.dialog.handler;

import geogebra.common.gui.InputHandler;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

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
	}
	
	/**
	 * 
	 * @return current geo
	 */
	public GeoElement getGeoElement(){
		return geo;
	}

	public boolean processInput(String inputValue) {			
		if (inputValue == null)
			return false;
		if (inputValue.equals(this.oldString)) return true; // Michael Borcherds 2007-12-31
		try {
			GeoElement newGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(
					geo, inputValue, true, true);
			app.getKernel().clearJustCreatedGeosInViews();
			app.doAfterRedefine(newGeo);
			
            // update after redefine
			// http://code.google.com/p/geogebra/issues/detail?id=147
            if (newGeo != null) {
                            setGeoElement(newGeo);
                            oldString = inputValue;
            }
            // -----------------------------------------------------------
            
			return newGeo != null;
		} catch (Exception e) {
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		return false;
	}
}
