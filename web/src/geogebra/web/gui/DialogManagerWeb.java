package geogebra.web.gui;

import geogebra.common.awt.Point;
import geogebra.common.gui.dialog.DialogManager;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.main.AbstractApplication;
import geogebra.web.main.Application;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;

public class DialogManagerWeb extends DialogManager {

	public DialogManagerWeb(AbstractApplication app) {
	    super(app);
    }

	@Override
    public boolean showFunctionInspector(GeoFunction geoFunction) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void showPropertiesDialog(ArrayList<GeoElement> geos) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public NumberValue showNumberInputDialog(String title, String message,
            String initText) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Object[] showAngleInputDialog(String title, String message,
            String initText) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    protected String prompt(String message, String def) {
	    return Window.prompt(message, def);
    }

	@Override
    protected boolean confirm(String string) {
	    return Window.confirm(string);
    }

	@Override
    public void closeAll() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showRenameDialog(GeoElement geo, boolean b, String label,
            boolean c) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
	protected void showTextDialog(GeoText geo, GeoPointND startPoint) {
		
		String inputValue = prompt("Enter text", "");

		if ((inputValue != null) ? !"".equals(inputValue) : false) {
			
			if (inputValue.indexOf('\"') == -1) {
				inputValue = "\"" + inputValue + "\"";
			}

			GeoElement[] ret = app.getKernel().getAlgebraProcessor()
					.processAlgebraCommand(inputValue, false);
			if (ret != null && ret[0].isTextValue()) {
				GeoText t = (GeoText) ret[0];

				if (startPoint.isLabelSet()) {
					try {
						t.setStartPoint(startPoint);
					} catch (Exception e) {
					}
				} else {

					Coords coords = startPoint.getInhomCoordsInD(3);
					t.setRealWorldLoc(coords.getX(), coords.getY());
					t.setAbsoluteScreenLocActive(false);
				}

				// make sure (only) the output of the text tool is selected
				app.getActiveEuclidianView()
						.getEuclidianController()
						.memorizeJustCreatedGeos(ret);

				t.updateRepaint();
				app.storeUndoInfo();
			}
		}

	}

	@Override
    public void showOptionsDialog(int tabEuclidian) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showPropertiesDialog() {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void showToolbarConfigDialog() {
	    // TODO Auto-generated method stub
	    
    }


	@Override
    public NumberValue showNumberInputDialog(String title, String message,
            String initText, boolean changingSign, String checkBoxText) {
	    // TODO Auto-generated method stub
	    return null;
    }


}
