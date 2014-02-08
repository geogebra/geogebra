package geogebra.web.gui.properties;

import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.OptionType;
import geogebra.web.gui.dialog.options.OptionPanelW;
import geogebra.web.gui.dialog.options.OptionsAdvancedW;
import geogebra.web.gui.dialog.options.OptionsCASW;
import geogebra.web.gui.dialog.options.OptionsDefaultsW;
import geogebra.web.gui.dialog.options.OptionsEuclidianW;
import geogebra.web.gui.dialog.options.OptionsLayoutW;
import geogebra.web.gui.dialog.options.OptionsObjectW;
import geogebra.web.gui.dialog.options.OptionsSpreadsheetW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
		private OptionsLayoutW layoutPanel;

		private PropertiesStyleBarW styleBar;

		private VerticalPanel mainPanel;

	public PropertiesViewW(AppW app) {
		super(app);
	    this.wrappedPanel = new PopupPanel();
	    
	    app.setPropertiesView(this);
	    
	    app.setWaitCursor();   
	    getOptionPanel(OptionType.OBJECTS);
	    
	    initGUI();
    }
	
	public void initGUI() {

		wrappedPanel.addStyleName("PropertiesViewW");
//		getStyleBar();
		//add(getStyleBar(), BorderLayout.NORTH);

		mainPanel = new VerticalPanel();
		mainPanel.add(((OptionPanelW) objectPanel).getWrappedPanel());
		wrappedPanel.add(mainPanel);

		//createButtonPanel();
		//add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	/**
	 * @return the style bar for this view.
	 */
	public PropertiesStyleBarW getStyleBar() {
		if (styleBar == null) {
			styleBar = newPropertiesStyleBar();
		}

		return styleBar;
	}
	
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBarW(this, (AppW) app);
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
				defaultsPanel = new OptionsDefaultsW((AppW) app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASW((AppW) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianW((AppW) app,
						((AppW) app).getActiveEuclidianView());
				euclidianPanel.setLabels();
				euclidianPanel.setView(((AppW)app).getEuclidianView1());
				euclidianPanel.showCbView(false);
			}
			
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView2());
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((AppW)app).getEuclidianView2());
				euclidianPanel2.showCbView(false);
			}
			
			return euclidianPanel2;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetW((AppW)app, ((AppW)app)
						.getGuiManager().getSpreadsheetView());
			}
			return spreadsheetPanel;

		case ADVANCED:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvancedW((AppW) app);
			}
			return advancedPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutW((AppW) app);
			}
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObjectW((AppW) app, false);
				((OptionsObjectW) objectPanel).setMinimumSize(((OptionsObjectW) objectPanel).getPreferredSize());
				
			} else {
				OptionsObjectW op =	getObjectPanel();
				op.updateGUI();
				op.selectTab(0);
			}
			
			return (OptionPanelW) objectPanel;
		}
		return null;
	}

	private OptionsObjectW getObjectPanel() {
		return (OptionsObjectW) objectPanel;
	}
	
	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
		App.debug("add(geo)");
	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
		App.debug("remove(geo)");
	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub
		getObjectPanel().updateIfInSelection(geo);	
		App.debug("update(geo)");
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		App.debug("update visual style");
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
	}

	public void reset() {
		// TODO Auto-generated method stub
		App.debug("reset");
	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void setMode(int mode,ModeSetter m) {
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
		App.debug("updateSelection");// TODO Auto-generated method stub
		getObjectPanel().updateGUI();
	}


	@Override
	public void setOptionPanel(OptionType type) {
		
	
	}
	
	@Override
	public void mousePressedForPropertiesView() {
		objectPanel.forgetGeoAdded();
    }

	@Override
    public void updateSelection(ArrayList<GeoElement> geos) {
		getObjectPanel().updateGUI();
	   App.debug("updateSelection(geos)"); 
    }

	@Override
    protected void updateTitleBar() {
	    // TODO Auto-generated method stub
	    
    }
	
	@Override
	public void attachView() {
		if (attached){
			App.debug("already attached");
			return;
		}
		
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		app.getKernel().getAnimatonManager().stopAnimation();
		attached = true;
	}
	
	@Override
	public void detachView() {
		kernel.detach(this);
		clearView();
		app.getKernel().getAnimatonManager().startAnimation();
		attached = false;
	}

	@Override
    public void updatePropertiesView() {
		App.debug("updatePropertiesView");
    }

	public void repaint() {
		App.debug("repaint");
    }

	public boolean isShowing() {
		App.debug("unimplemented");
	    return false;
    }

	public Widget getWrappedPanel() {
	    return wrappedPanel;
    }
}
