package geogebra.gui;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.CopyPaste;
import geogebra.util.Unicode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class RenameInputHandler implements InputHandler {
	private GeoElement geo;

	private boolean storeUndo;

	private Application app;

	private static Set<String> invalidFunctionNames = new HashSet<String>(ExpressionNodeConstants.RESERVED_FUNCTION_NAMES);
	static
	{
		invalidFunctionNames.addAll(Arrays.asList("x", "y", Unicode.IMAGINARY));
	}


	public RenameInputHandler(Application app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public boolean processInput(String inputValue) {
		GeoElement geo = this.geo;
		
		if (inputValue == null || inputValue.equals(geo.getLabel()))
			return false;
		
		if (!checkName(geo, inputValue)) {
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
				String tempLabel = existingGeo.getIndexLabel(newLabel);
				existingGeo.rename(tempLabel);				
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

	// check if name is valid for geo
	public static boolean checkName(GeoElement geo, String name) {
		if (name == null) return true;

		if (name.startsWith(CopyPaste.labelPrefix))
			return false;

		name = name.toLowerCase(Locale.US);
		if (geo.isGeoFunction()) {
			if (invalidFunctionNames.contains(name))
					return false;
		}

		return true;
	}
}
