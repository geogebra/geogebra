package geogebra.web.gui.properties;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.dialog.options.OptionsObjectW;
import geogebra.web.main.Application;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author gabor
 * 
 * PropertiesView for Web
 *
 */
public class PropertiesViewW extends
        geogebra.common.gui.view.properties.PropertiesView {
	
	private PopupPanel wrappedPanel;

	public PropertiesViewW(Application app) {
	    this.wrappedPanel = new PopupPanel();
	    this.app = app;
	    
	    app.setPropertiesView(this);
	    
	    app.setWaitCursor();
	    
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
	
	OptionsObjectW objectPanel;
	
	@Override
	public void mousePressedForPropertiesView() {
		objectPanel.forgetGeoAdded();
    }

}
