package org.geogebra.web.web.gui.properties;

import java.util.ArrayList;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.tabpanel.MultiRowsTabPanel;
import org.geogebra.web.web.gui.dialog.options.OptionPanelW;
import org.geogebra.web.web.gui.dialog.options.OptionsAdvancedW;
import org.geogebra.web.web.gui.dialog.options.OptionsAlgebraW;
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
public class PropertiesViewW extends PropertiesView
		implements RequiresResize, SetLabels {


	private FlowPanel wrappedPanel;

	// option panels
	private OptionsDefaultsW defaultsPanel;
	private OptionsEuclidianW euclidianPanel, euclidianPanel2, euclidianPanel3D;
	private OptionsSpreadsheetW spreadsheetPanel;
	private OptionsCASW casPanel;
	private OptionsAdvancedW advancedPanel;
	private OptionsLayoutW layoutPanel;
	private OptionsAlgebraW algebraPanel;
	
	// current OptionPanel
	private OptionPanelW optionPanel;

	private PropertiesStyleBarW styleBar;

	private Label notImplemented;

	private FlowPanel contentsPanel;
	private OptionType optionType;

	// For autoopen AV feature
	// private boolean wasAVShowing;
	//
	// private boolean auxWasVisible;
	//
	// private boolean isObjectOptionsVisible;
	/**
	 * 
	 * @param app
	 *            app
	 * @param ot
	 *            initial options type
	 */
	public PropertiesViewW(AppW app, OptionType ot) {
		super(app);
		this.wrappedPanel = new FlowPanel();
		app.setPropertiesView(this);

		app.setWaitCursor();   

		notImplemented = new Label("Not implemented");
		optionType = ot;
		initGUI();
		app.setDefaultCursor();
	}

	private void initGUI() {

		wrappedPanel.addStyleName("PropertiesViewW");
		//		getStyleBar();

		//mainPanel = new FlowPanel();
		
		contentsPanel = new FlowPanel();
		contentsPanel.addStyleName("contentsPanel");
		contentsPanel.addStyleName("contentsPanel2");
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

	/**
	 * @return properties stylebar
	 */
	protected PropertiesStyleBarW newPropertiesStyleBar() {
		return new PropertiesStyleBarW(this, app);
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 *            panel type
	 * @param subType
	 *            tab number for given panel
	 * @return options panel
	 */
	public OptionPanelW getOptionPanel(OptionType type, int subType) {
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

			Log.debug("euclidianPanel");
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView2(1));
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((AppW)app).getEuclidianView2(1));
				euclidianPanel2.showCbView(false);
			}
			Log.debug("euclidianPanel2");
			return euclidianPanel2;
		case EUCLIDIAN3D:
			if (euclidianPanel3D == null) {
				euclidianPanel3D = new OptionsEuclidianW((AppW) app,
						((AppW)app).getEuclidianView3D());
				euclidianPanel3D.setLabels();
		//		euclidianPanel3D.setView(((AppW)app).getEuclidianView3D());
				euclidianPanel3D.showCbView(false);
			}
			Log.debug("euclidianPanel2");
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

		case ALGEBRA:
			if (algebraPanel == null) {
				algebraPanel = new OptionsAlgebraW((AppW) app);
			}
			return algebraPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutW((AppW) app);
			}
			layoutPanel.getWrappedPanel().setStyleName("layoutPanel");
			
			return layoutPanel;

		case OBJECTS:
			if (getObjectPanel() == null) {
				setObjectPanel(new OptionsObjectW((AppW) app, false,
						new Runnable() {

							@Override
							public void run() {
								updatePropertiesView();
							}
						}));

			}

			Log.debug("obect prop SELECTING TAB " + subType);
			getObjectPanel().selectTab(subType);
			return getObjectPanel();
		}
		return null;
	}

	/**
	 * TODO disabled; decide if we want this
	 * 
	 * @param visible
	 *            whether to show AV
	 */
	public void updateAVvisible(boolean visible) {
		// if ((visible && this.optionPanel instanceof OptionsObjectW) ==
		// this.isObjectOptionsVisible) {
		// return;
		// }
		// this.isObjectOptionsVisible = !this.isObjectOptionsVisible;
		// if (visible) {
		// wasAVShowing = app.getGuiManager().hasAlgebraViewShowing();
		// auxWasVisible = app.getSettings().getAlgebra()
		// .getShowAuxiliaryObjects();
		// if (!wasAVShowing) {
		// app.getGuiManager().setShowView(true, App.VIEW_ALGEBRA);
		// app.updateViewSizes();
		// }
		// app.setShowAuxiliaryObjects(true);
		//
		// } else {
		// if (!auxWasVisible) {
		// app.setShowAuxiliaryObjects(false);
		// }
		// if (!wasAVShowing) {
		// app.getGuiManager().setShowView(false, App.VIEW_ALGEBRA);
		// app.updateViewSizes();
		// }
		// }

	}

	@Override
	protected OptionsObjectW getObjectPanel() {
		return super.getObjectPanel() != null
				? (OptionsObjectW) super.getObjectPanel() : null;
	}

	@Override
	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub
	}	



	@Override
	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(GeoElement geo) {
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		// TODO Auto-generated method stub
		Log.debug("update visual style");
		if(geo.isLabelSet()){
			updatePropertiesGUI();
		}
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		updatePropertiesGUI();

	}

	@Override
	public void repaintView() {
		// nothing on repaint
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		Log.debug("reset");
	}

	@Override
	public void clearView() {
		Log.debug("Clear View");
	}

	@Override
	public void setMode(int mode,ModeSetter m) {
		// TODO Auto-generated method stub
		Log.debug("setting mode");
	}

	@Override
	public int getViewID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
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
			if (optionType != OptionType.EUCLIDIAN
					|| optionType != OptionType.EUCLIDIAN2
					|| optionType != OptionType.EUCLIDIAN3D
					|| optionType != OptionType.EUCLIDIAN_FOR_PLANE) {
				if (app.getActiveEuclidianView().isEuclidianView3D()) {
					setOptionPanel(OptionType.EUCLIDIAN3D);
				} else if (app.getActiveEuclidianView().isDefault2D()) {
					setOptionPanel(app.getActiveEuclidianView().getEuclidianViewNo() == 1
						? OptionType.EUCLIDIAN : OptionType.EUCLIDIAN2);
				} else {
					setOptionPanel(OptionType.EUCLIDIAN_FOR_PLANE);
				}
			}
		}


		updatePropertiesGUI();
	}

	@Override
	protected void setOptionPanelWithoutCheck(OptionType type) {
		int sType = 0;
		if (type == OptionType.OBJECTS && this.getObjectPanel() != null) {
			MultiRowsTabPanel tabPanel = this.getObjectPanel()
					.getTabPanel();
			sType = tabPanel.getTabBar().getSelectedTab();

		}
		setOptionPanel(type, sType);
	}

	@Override
	protected void setObjectsToolTip() {
		Log.debug("=============== PropertiesViewW.setObjectsToolTip() : TODO");
		// styleBar.setObjectsToolTip();
	}

	@Override
	protected void setSelectedTab(OptionType type) {
		switch (type) {
		default:
			// do nothing
			break;
		case EUCLIDIAN:
			euclidianPanel.setSelectedTab(selectedTab);
			break;
		case EUCLIDIAN2:
			euclidianPanel2.setSelectedTab(selectedTab);
			break;
		}
	}

	@Override
	protected void updateObjectPanelSelection(ArrayList<GeoElement> geos) {
		if (getObjectPanel() == null) {
			return;
		}
		getObjectPanel().updateSelection(geos);
		updateTitleBar();
		setObjectsToolTip();
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		optionType = type;
		contentsPanel.clear();
		optionPanel = getOptionPanel(type, subType);
		updateAVvisible(true);
		Widget wPanel = optionPanel.getWrappedPanel();
		notImplemented.setText(getTypeString(type) + " - Not implemented");
		contentsPanel.add(wPanel != null ? wPanel: notImplemented);
		if(wPanel != null) {
			onResize();
		}
		this.styleBar.selectButton(type);
	}

	/**
	 * @return selected option type
	 */
	public OptionType getOptionType() {
		return optionType;
	}

	@Override
	public void mousePressedForPropertiesView() {
		if (getObjectPanel() == null) {
			return;
		}
		getObjectPanel().forgetGeoAdded();
	}


	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {
		if (geos.size() != 0 && optionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}
		updatePropertiesGUI();
		Log.debug("updateSelection(geos)");
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
		// Log.debug("selecting tab 2");
		//			   getObjectPanel().selectTab(2);
		//		   }

		if (styleBar != null) {
			styleBar.updateGUI();
		}	


	}

	@Override
	protected void updateTitleBar() {
		updatePropertiesGUI();

	}

	@Override
	public void attachView() {
		if (isAttached()) {
			Log.debug("already attached");
			return;
		}

		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		app.getKernel().getAnimatonManager().stopAnimation();
		setAttached(true);
	}

	@Override
	public void detachView() {
		kernel.detach(this);
		clearView();
		app.getKernel().getAnimatonManager().startAnimation();
		setAttached(false);
	}

	@Override
	public void updatePropertiesView() {
		updatePropertiesGUI();
		Log.debug("updatePropertiesView");
	}


	@Override
	public boolean isShowing() {
		Log.debug("isShowing");
		return false;
	}

	/**
	 * 
	 * @return GWT panel of this view
	 */
	public Widget getWrappedPanel() {
		return wrappedPanel;
	}

	/**
	 * Rebuild GUI for the new font size
	 */
	public void updateFonts(){
		updatePropertiesGUI();
	}

    @Override
	public void onResize() {
    	//-34px for width of stylebar
    	int width = getWrappedPanel().getOffsetWidth() - 37;
    	int height = getWrappedPanel().getOffsetHeight();
    	//contentsPanel.setHeight(getWrappedPanel().getOffsetHeight() + "px");
    	
    	if(height > 0 && width > 0) {
    		contentsPanel.setWidth(width + "px");
    		

    	}
    }
    
	@Override
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
		if (casPanel != null) {
			casPanel.setLabels();
		}

		if (algebraPanel != null) {
			algebraPanel.setLabels();
		}
    }

	@Override
	public void updateStyleBar() {

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}
}
