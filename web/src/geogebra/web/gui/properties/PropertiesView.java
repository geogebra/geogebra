package geogebra.web.gui.properties;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.dialog.options.OptionsObject;

import java.util.ArrayList;

public class PropertiesView extends
        geogebra.common.gui.view.properties.PropertiesView {

	public PropertiesView(AbstractApplication app) {
	    // TODO Auto-generated constructor stub
    }

	public void add(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void setMode(int mode) {
		// TODO Auto-generated method stub

	}

	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOptionPanel(OptionType type) {
		// TODO Auto-generated method stub

	}
	
	OptionsObject objectPanel;
	
	@Override
	public void mousePressedForPropertiesView() {
		objectPanel.forgetGeoAdded();
    }

}
