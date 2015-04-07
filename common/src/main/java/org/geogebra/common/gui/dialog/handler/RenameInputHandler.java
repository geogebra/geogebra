package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.gui.InputHandler;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.Construction.Constants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;

public class RenameInputHandler implements InputHandler {
	private GeoElement geo;

	private boolean storeUndo;

	private App app;

	

	public RenameInputHandler(App app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public boolean processInput(String inputValue) {
		GeoElement geo = this.geo;
		
		if (inputValue == null)
			return false;

		if (inputValue.equals(geo.getLabel(StringTemplate.defaultTemplate)))
			return true;

		if (!LabelManager.checkName(geo, inputValue)) {
			app.showError("InvalidInput", inputValue);
			return false;
		}

		try {
			Kernel kernel = app.getKernel();
			String newLabel = kernel.getAlgebraProcessor().parseLabel(
					inputValue);

			// is there a geo with this name?
			GeoElement existingGeo = kernel.lookupLabel(newLabel);	
			
			if (existingGeo != null) {
				// rename this geo too:
				if (kernel.getConstruction().isConstantElement(existingGeo)==Constants.NOT){					
					String tempLabel = existingGeo.getIndexLabel(newLabel);
					existingGeo.rename(tempLabel);
				}else
					newLabel = existingGeo.getIndexLabel(newLabel);
			}					

			if (geo.rename(newLabel) && storeUndo) {
				app.storeUndoInfo();
			}

			return true;
		} catch (Exception e) {
			app.showError("InvalidInput", inputValue);
		} catch (MyError err) {
			app.showError("InvalidInput", inputValue);
		}
		
		return false;
	}

	
}
