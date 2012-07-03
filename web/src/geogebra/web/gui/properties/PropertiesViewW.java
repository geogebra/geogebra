package geogebra.web.gui.properties;

import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.dialog.options.OptionPanelW;
import geogebra.web.gui.dialog.options.OptionsAdvancedW;
import geogebra.web.gui.dialog.options.OptionsCASW;
import geogebra.web.gui.dialog.options.OptionsDefaultsW;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.gui.dialog.options.OptionsLayoutW;
import geogebra.web.gui.dialog.options.OptionsObjectW;
import geogebra.web.gui.dialog.options.OptionsSpreadsheetW;
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
	
	// option panels
		private OptionsDefaultsW defaultsPanel;
		private OptionsEuclidianW euclidianPanel, euclidianPanel2;
		private OptionsSpreadsheetW spreadsheetPanel;
		private OptionsCASW casPanel;
		private OptionsAdvancedW advancedPanel;
		private OptionsObjectW objectPanel;
		private OptionsLayoutW layoutPanel;

	public PropertiesViewW(Application app) {
	    this.wrappedPanel = new PopupPanel();
	    this.app = app;
	    
	    kernel = app.getKernel();
	    
	    app.setPropertiesView(this);
	    
	    app.setWaitCursor();   
	    getOptionPanel(OptionType.OBJECTS);
	    
    }
	
	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 * @return
	 */
	public OptionPanelW getOptionPanel(OptionType type) {
		
		//AbstractApplication.printStacktrace("type :"+type);

		switch (type) {
		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaultsW((Application) app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASW((Application) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianW((Application) app,
						((Application) app).getActiveEuclidianView());
				euclidianPanel.setLabels();
				euclidianPanel.setView(((Application)app).getEuclidianView1());
				euclidianPanel.showCbView(false);
			}
			
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((Application) app,
						((Application)app).getEuclidianView2());
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((Application)app).getEuclidianView2());
				euclidianPanel2.showCbView(false);
			}
			
			return euclidianPanel2;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetW((Application)app, ((Application)app)
						.getGuiManager().getSpreadsheetView());
			}
			return spreadsheetPanel;

		case ADVANCED:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvancedW((Application) app);
			}
			return advancedPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutW((Application) app);
			}
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObjectW((Application) app);
				objectPanel.setMinimumSize(objectPanel.getPreferredSize());
			}
			return objectPanel;
		}
		return null;
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
	
	@Override
	public void mousePressedForPropertiesView() {
		objectPanel.forgetGeoAdded();
    }

}
