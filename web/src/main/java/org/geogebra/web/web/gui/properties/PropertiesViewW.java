package org.geogebra.web.web.gui.properties;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.dialog.options.OptionPanelW;
import org.geogebra.web.web.gui.dialog.options.OptionsAdvancedW;
import org.geogebra.web.web.gui.dialog.options.OptionsCASW;
import org.geogebra.web.web.gui.dialog.options.OptionsDefaultsW;
import org.geogebra.web.web.gui.dialog.options.OptionsEuclidianW;
import org.geogebra.web.web.gui.dialog.options.OptionsLayoutW;
import org.geogebra.web.web.gui.dialog.options.OptionsObjectW;
import org.geogebra.web.web.gui.dialog.options.OptionsSpreadsheetW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author gabor
 * 
 * PropertiesView for Web
 *
 */
public class PropertiesViewW extends
org.geogebra.common.gui.view.properties.PropertiesView implements RequiresResize, SetLabels {

	private static final String AlgebraViewWeb = null;

	private FlowPanel wrappedPanel;

	// option panels
	private OptionsDefaultsW defaultsPanel;
	private OptionsEuclidianW euclidianPanel, euclidianPanel2, euclidianPanel3D;
	private OptionsSpreadsheetW spreadsheetPanel;
	private OptionsCASW casPanel;
	private OptionsAdvancedW advancedPanel;
	private OptionsLayoutW layoutPanel;
	
	// current OptionPanel
	private OptionPanelW optionPanel;

	private PropertiesStyleBarW styleBar;

	private FlowPanel mainPanel;
	private Label notImplemented;

	private FlowPanel contentsPanel;
	private OptionType optionType;

	public PropertiesViewW(AppW app) {
		super(app);
		this.wrappedPanel = new FlowPanel();
		app.setPropertiesView(this);

		app.setWaitCursor();   

		notImplemented = new Label("Not implemented");
		optionType = OptionType.EUCLIDIAN;
		initGUI();
	}

	public void initGUI() {

		wrappedPanel.addStyleName("PropertiesViewW");
		//		getStyleBar();

		//mainPanel = new FlowPanel();
		
		contentsPanel = new FlowPanel();
		contentsPanel.addStyleName("contentsPanel");
		//wrappedPanel.addStyleName("propertiesView");
		//mainPanel.add(contentsPanel);
		wrappedPanel.add(contentsPanel);
		wrappedPanel.add(getStyleBar().getWrappedPanel());
		
//		if(!((AppW) app).getLAF().isSmart()){
		//mainPanel.add(getStyleBar().getWrappedPanel());
		//	}
			
		//wrappedPanel.add(mainPanel);

		setOptionPanel(optionType, 0);
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
		return new PropertiesStyleBarW(this, app);
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 * @return
	 */
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
		App.debug("[OptionPanelW] getOptionPanel");
		//AbstractApplication.printStacktrace("type :"+type);
		if (styleBar != null) {
			styleBar.updateGUI();
		}	
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

			App.debug("euclidianPanel");
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView2(1));
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((AppW)app).getEuclidianView2(1));
				euclidianPanel2.showCbView(false);
			}
			App.debug("euclidianPanel2");
			return euclidianPanel2;
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
		//		euclidianPanel3D.setView(((AppW)app).getEuclidianView3D());
				euclidianPanel3D.showCbView(false);
			}
			App.debug("euclidianPanel2");
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
			layoutPanel.getWrappedPanel().setStyleName("layoutPanel");
			
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObjectW((AppW) app, false);
				((OptionsObjectW) objectPanel).setMinimumSize(((OptionsObjectW) objectPanel).getPreferredSize());

			} else {
				OptionsObjectW op =	getObjectPanel();
				//op.reinit();
				//op.selectTab(subType);
			}
			App.debug("obect prop SELECTING TAB " + subType);
			((OptionsObjectW) objectPanel).selectTab(subType);
			return (OptionPanelW) objectPanel;
		}
		return null;
	}
	private OptionsObjectW getObjectPanel() {
		return objectPanel != null ? (OptionsObjectW) objectPanel:null;
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
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
		App.debug("update(geo)");
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		App.debug("update visual style");
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		updatePropertiesGUI();

	}

	public void repaintView() {
	}

	public void reset() {
		// TODO Auto-generated method stub
		App.debug("reset");
	}

	public void clearView() {
		App.debug("Clear View");
	}

	public void setMode(int mode,ModeSetter m) {
		// TODO Auto-generated method stub
		App.debug("setting mode");
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
		if (app.getSelectionManager().selectedGeosSize() != 0 && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		else if (app.getSelectionManager().selectedGeosSize() == 0) {
			if (optionType != OptionType.EUCLIDIAN ||  optionType != OptionType.EUCLIDIAN2 ||
					optionType != OptionType.EUCLIDIAN3D) {
				if (app.getActiveEuclidianView().isEuclidianView3D()) {
					setOptionPanel(OptionType.EUCLIDIAN3D);
				} else {
					setOptionPanel(app.getActiveEuclidianView().getEuclidianViewNo() == 1
						? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2);
				}
			}
		}


		updatePropertiesGUI();
	}

	@Override
	public void setOptionPanel(OptionType type) {
		setOptionPanel(type, 0);
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		optionType = type;
		contentsPanel.clear();
		optionPanel = getOptionPanel(type, subType);
		Widget wPanel = optionPanel.getWrappedPanel();
		notImplemented.setText(getTypeString(type) + " - Not implemented");
		contentsPanel.add(wPanel != null ? wPanel: notImplemented);
		if(wPanel != null) {
			onResize();
		}
		this.styleBar.selectButton(type);
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (objectPanel == null) {
			return;
		}
		objectPanel.forgetGeoAdded();
	}


	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		if (geos.size() != 0 && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		updatePropertiesGUI();
		App.debug("updateSelection(geos)"); 
	}

	private void updatePropertiesGUI() {
		OptionsObjectW panel = getObjectPanel();
		if (panel != null) {
			panel.updateGUI();
			if (optionType == OptionType.OBJECTS) {
				if (!panel.getWrappedPanel().isVisible()) {
					setOptionPanel(OptionType.EUCLIDIAN);
				}
			}
		}

		//		   if (optionType == OptionType.OBJECTS)  {
		//			   App.debug("selecting tab 2");
		//			   getObjectPanel().selectTab(2);
		//		   }

		if (styleBar != null) {
			styleBar.updateGUI();
		}	


	}

	@Override
	protected void updateTitleBar() {
		app.debug("updateTitleBar()");
		updatePropertiesGUI();
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
		updatePropertiesGUI();
		App.debug("updatePropertiesView");
	}

	public void repaint() {
		App.debug("repaint");
	}

	public boolean isShowing() {
		App.debug("isShowing");
		return false;
	}

	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	public void updateFonts(){
		updatePropertiesGUI();
	}

    public void onResize() {
    	//-34px for width of stylebar
    	int width = getWrappedPanel().getOffsetWidth() - 37;
    	int height = getWrappedPanel().getOffsetHeight();
    	//contentsPanel.setHeight(getWrappedPanel().getOffsetHeight() + "px");
    	
    	if(height > 0 && width > 0) {
    		contentsPanel.setWidth(width + "px");
    		
    		//-30px for Tabs, -27px for padding, -26px for paddings
        	optionPanel.onResize((height - 30 - 27), width - 26);
    	}
    }
    
	public boolean suggestRepaint(){
		return false;
	}

	@Override
    public void setLabels() {
		if (euclidianPanel != null) {
			euclidianPanel.setLabels();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.setLabels();
		}
		if (euclidianPanel3D != null) {
			euclidianPanel3D.setLabels();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.setLabels();
		}
    }
}
