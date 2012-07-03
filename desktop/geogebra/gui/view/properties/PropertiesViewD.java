/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.properties;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.dialog.options.OptionPanelD;
import geogebra.gui.dialog.options.OptionsAdvancedD;
import geogebra.gui.dialog.options.OptionsCASD;
import geogebra.gui.dialog.options.OptionsDefaultsD;
import geogebra.gui.dialog.options.OptionsEuclidianD;
import geogebra.gui.dialog.options.OptionsLayoutD;
import geogebra.gui.dialog.options.OptionsObjectD;
import geogebra.gui.dialog.options.OptionsSpreadsheetD;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * View for properties
 * 
 * @author mathieu
 * @version
 */
public class PropertiesViewD extends geogebra.common.gui.view.properties.PropertiesView {

	private static final long serialVersionUID = 1L;

	//private GeoTree geoTree;

	private PropertiesStyleBar styleBar;
	private JPanel wrappedPanel;

	

	// option panels
	private OptionsDefaultsD defaultsPanel;
	private OptionsEuclidianD euclidianPanel, euclidianPanel2;
	private OptionsSpreadsheetD spreadsheetPanel;
	private OptionsCASD casPanel;
	private OptionsAdvancedD advancedPanel;
	private OptionsObjectD objectPanel;
	private OptionsLayoutD layoutPanel;

	

	// GUI elements
	private JPanel mainPanel, buttonPanel;
	//private JButton restoreDefaultsButton, saveButton;

	private boolean isIniting = true;

	/**************************************************
	 * Constructor
	 * @param app
	 */
	public PropertiesViewD(Application app) {

		this.wrappedPanel = new JPanel();
		this.app = app;
		app.setPropertiesView(this);
		
		//init object properties
		AbstractApplication.debug("init object properties");
		app.setWaitCursor();
		getOptionPanel(OptionType.OBJECTS);
		AbstractApplication.debug("end (init object properties)");
		
		kernel = app.getKernel();
		// this.geoTree=geoTree;

		initGUI();
		setOptionPanel(selectedOptionType);
		isIniting = false;
		styleBar.getBtnOption().requestFocus();
		

		setLabels();
		app.setDefaultCursor();//remove this if init object properties is faster
	}

	// ============================================
	// GUI
	// ============================================

	/** */
	public void initGUI() {

		wrappedPanel.setLayout(new BorderLayout());
		getStyleBar();
		//add(getStyleBar(), BorderLayout.NORTH);

		mainPanel = new JPanel(new BorderLayout());
		wrappedPanel.add(mainPanel, BorderLayout.CENTER);

		//createButtonPanel();
		//add(buttonPanel, BorderLayout.SOUTH);
		
	}

	
	/*
	 * Creates the button panel for all option panels except the Object Panel
	 *
	private void createButtonPanel() {
	
		// panel with buttons at the bottom
		buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createMatteBorder(1, 0, 0, 0, SystemColor.controlLtHighlight),
				BorderFactory.createEmptyBorder(5, 0, 5, 0)));

		// buttonPanel.setBackground(Color.white);

		// (restore defaults on the left side)
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// panel.setBackground(Color.white);

		if (!app.isApplet()) {
			restoreDefaultsButton = new JButton();
			restoreDefaultsButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferences.getPref().clearPreferences();

					// reset defaults for GUI, views etc
					// this has to be called before load XML preferences,
					// in order to avoid overwrite
					app.getSettings().resetSettings();

					// for geoelement defaults, this will do nothing, so it is
					// OK here
					GeoGebraPreferences.getPref().loadXMLPreferences(app);

					// reset default line thickness etc
					app.getKernel().getConstruction().getConstructionDefaults()
							.resetDefaults();

					// reset defaults for geoelements; this will create brand
					// new objects
					// so the options defaults dialog should be reset later
					app.getKernel().getConstruction().getConstructionDefaults()
							.createDefaultGeoElementsFromScratch();

					// reset the stylebar defaultGeo
					if (app.getEuclidianView1().hasStyleBar())
						app.getEuclidianView1().getStyleBar()
								.restoreDefaultGeo();
					if (app.hasEuclidianView2EitherShowingOrNot())
						if (app.getEuclidianView2().hasStyleBar())
							app.getEuclidianView2().getStyleBar()
									.restoreDefaultGeo();

					// restore dialog panels to display these defaults
					restoreDefaults();

				}
			});

			panel.add(restoreDefaultsButton);
		}

		buttonPanel.add(panel, BorderLayout.WEST);

		
		// (save and close on the right side)
		panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// panel.setBackground(Color.white);

		
		if (!app.isApplet()) {
			saveButton = new JButton();
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferences.getPref().saveXMLPreferences(app);
				}
			});
			panel.add(saveButton);
		}
		

		buttonPanel.add(panel, BorderLayout.EAST);
		
	
	}
	
	*/

	/**
	 * Restores default settings in option dialogs
	 */
	public void restoreDefaults() {

		((OptionsDefaultsD) getOptionPanel(OptionType.DEFAULTS))
				.restoreDefaults();
		((OptionsAdvancedD) getOptionPanel(OptionType.ADVANCED))
				.updateAfterReset();

		// TODO
		// --- add calls to other panels here

		updateGUI();
	}

	
	// ============================================
	// Updates
	// ============================================

	/**
	 * Updates properties view panel. If any geos are selected then the Objects
	 * panel will be shown. If not, then an option pane for the current focused
	 * view is shown. 
	 * Called when a view gets the focus.
	 */
	public void updatePropertiesView() {

		setOptionPanelRegardingFocus(false);
	}

	
	
	/**
	 * Updates properties view panel. If geos are not empty then the Objects
	 * panel will be shown. If not, then an option pane for the current focused
	 * view is shown. 
	 * @param geosList geos list
	 */
	private void updatePropertiesViewCheckConstants(ArrayList<GeoElement> geosList) {
		
		//remove constant geos
		ArrayList<GeoElement> geos = removeAllConstants(geosList);
		
		updatePropertiesView(geos);
	}
		
	private void updatePropertiesView(ArrayList<GeoElement> geos) {
	
		if (geos.size() > 0) {
			setOptionPanel(OptionType.OBJECTS,geos);
		} else {
			
			setOptionPanelRegardingFocus(true);

		}
	}
	
	final private void setOptionPanelRegardingFocus(boolean updateEuclidianTab){
		int focusedViewId = ((Application) app).getGuiManager().getLayout()
				.getDockManager().getFocusedViewId();


		if (viewMap.get(focusedViewId) != null) {
			OptionType type = viewMap.get(focusedViewId);
			if (type==OptionType.EUCLIDIAN || type==OptionType.EUCLIDIAN2){
				if (app.getActiveEuclidianView().getEuclidianController().hitCheckBoxOrTextfield()){
					//hit check box or text field : does nothing
					//setOptionPanel(OptionType.OBJECTS);
				}else{
					//ev clicked
					setOptionPanel(type);
					if (updateEuclidianTab){			
						switch(type){
						case EUCLIDIAN:
							euclidianPanel.setSelectedTab(selectedTab);
							break;
						case EUCLIDIAN2:
							euclidianPanel2.setSelectedTab(selectedTab);
							break;
						}
					}
				}
			}else
				setOptionPanel(type);
		} else {
			if (app.getSelectedGeos().size()==0){ //does nothing if some objects are selected
				setOptionPanel(OptionType.EUCLIDIAN);
				if (updateEuclidianTab)
					euclidianPanel.setSelectedTab(selectedTab);
			}
		}
	}
	
	/**
	 * acts when mouse has been pressed in euclidian controller
	 */
	public void mousePressedForPropertiesView(){
		objectPanel.forgetGeoAdded();
	}
	
	/**
	 * acts when mouse has been released in euclidian controller
	 * @param creatorMode says if euclidian view is in creator mode (ie not move mode)
	 */
	public void mouseReleasedForPropertiesView(boolean creatorMode){

		GeoElement geo = objectPanel.consumeGeoAdded();
		
		
		//AbstractApplication.debug("\ngeo="+geo+"\nsel0="+app.getSelectedGeos().get(0));
		if (app.getSelectedGeos().size()>0) //selected geo is the most important
			updatePropertiesViewCheckConstants(app.getSelectedGeos());
		else if (geo!=null){ //last created geo
			if (creatorMode){ //if euclidian view is e.g. in move mode, then geo was created by a script, so just show object properties
				ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
				geos.add(geo);
				setOptionPanel(OptionType.OBJECTS,geos);
			}else{
				setOptionPanel(OptionType.OBJECTS,null);
			}
		}else{ //focus
			updateSelectedTab(Construction.Constants.NOT);
			setOptionPanelRegardingFocus(true);
			//updatePropertiesView();
		}
	}

	/**
	 * @return type of option panel currently displayed
	 */
	public OptionType getSelectedOptionType() {
		return selectedOptionType;
	}
	
	
	/**
	 * Sets and shows the option panel for the given option type
	 * 
	 * @param type type
	 */
	public void setOptionPanel(OptionType type) {
		setOptionPanel(type,app.getSelectedGeos());
	}
		
	
	private void setOptionPanel(OptionType type, ArrayList<GeoElement> geos) {
		
		//AbstractApplication.printStacktrace("\ntype="+type+"\nisIniting="+isIniting+"\nsize="+app.getSelectedGeos().size());
		//AbstractApplication.debug("\ntype="+type+"\nisIniting="+isIniting+"\nsize="+app.getSelectedGeos().size()+"\ngeos="+geos);

		

		if (type == null) {
			return;
		}


		//update selection
		if (type==OptionType.OBJECTS){
			if (geos!=null)
				objectPanel.updateSelection(geos);	
			styleBar.setObjectsToolTip();
			
		}

		if (!isIniting && selectedOptionType == type) {
			updateTitleBar();
			return;
		}
			
				
		selectedOptionType = type;

		// clear the center panel and replace with selected option panel
		mainPanel.removeAll();
		getOptionPanel(type).setBorder(
				BorderFactory.createEmptyBorder(15, 10, 10, 10));
		mainPanel.add((JPanel) getOptionPanel(type).getWrappedPanel(), BorderLayout.CENTER);

				
		// don't show the button panel in the Objects panel (it has it's own)
		//buttonPanel.setVisible(type != OptionType.OBJECTS);

		// update GUI
		//updateGUI();
		getOptionPanel(type).updateGUI();
		getOptionPanel(type).revalidate();
		updateStyleBar();
		updateTitleBar(); 
		
		wrappedPanel.revalidate();
		wrappedPanel.repaint();
	}

	public void updateGUI() {

		if (defaultsPanel != null) {
			defaultsPanel.updateGUI();
		}
		if (euclidianPanel != null) {
			euclidianPanel.updateGUI();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.updateGUI();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.updateGUI();
		}
		if (casPanel != null) {
			casPanel.updateGUI();
		}
		if (advancedPanel != null) {
			advancedPanel.updateGUI();
		}
		if (layoutPanel != null) {
			layoutPanel.updateGUI();
		}
		if (objectPanel != null) {
			objectPanel.setVisible(selectedOptionType == OptionType.OBJECTS);
		}

		setLabels();

	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 * @return
	 */
	public OptionPanelD getOptionPanel(OptionType type) {
		
		//AbstractApplication.printStacktrace("type :"+type);

		switch (type) {
		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaultsD((Application) app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASD((Application) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianD((Application) app,
						((Application) app).getActiveEuclidianView());
				euclidianPanel.setLabels();
				euclidianPanel.setView(((Application)app).getEuclidianView1());
				euclidianPanel.showCbView(false);
			}
			
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianD((Application) app,
						((Application)app).getEuclidianView2());
				euclidianPanel2.setLabels();
				euclidianPanel2.setView(((Application)app).getEuclidianView2());
				euclidianPanel2.showCbView(false);
			}
			
			return euclidianPanel2;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetD((Application)app, ((Application)app)
						.getGuiManager().getSpreadsheetView());
			}
			return spreadsheetPanel;

		case ADVANCED:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvancedD((Application) app);
			}
			return advancedPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutD((Application) app);
			}
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObjectD((Application) app);
				objectPanel.setMinimumSize(objectPanel.getPreferredSize());
			}
			return objectPanel;
		}
		return null;
	}

	public String getTypeString(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getPlain("PropertiesOfA",app.getPlain("Defaults"));
		case SPREADSHEET:
			return app.getPlain("PropertiesOfA",app.getPlain("Spreadsheet"));
		case EUCLIDIAN:
			return app.getPlain("PropertiesOfA",app.getPlain("DrawingPad"));
		case EUCLIDIAN2:
			return app.getPlain("PropertiesOfA",app.getPlain("DrawingPad2"));
		case CAS:
			return app.getPlain("PropertiesOfA",app.getPlain("CAS"));
		case ADVANCED:
			return app.getPlain("PropertiesOfA",app.getMenu("Advanced"));
		case OBJECTS:
			//return app.getMenu("Objects");
			return objectPanel.getSelectionDescription();
		case LAYOUT:
			return app.getPlain("PropertiesOfA",app.getPlain("Layout"));
		}
		return null;
	}

	/**
	 * @return the style bar for this view.
	 */
	public PropertiesStyleBar getStyleBar() {
		if (styleBar == null) {
			styleBar = newPropertiesStyleBar();
		}

		return styleBar;
	}

	/**
	 * 
	 * @return new properties style bar
	 */
	protected PropertiesStyleBar newPropertiesStyleBar() {
		return new PropertiesStyleBar(this, (Application) app);
	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {

		/*
		if (!app.isApplet()) {
			saveButton.setText(app.getMenu("Settings.Save"));
			restoreDefaultsButton.setText(app.getMenu("Settings.ResetDefault"));
		}
		*/

		//GuiManager.setLabelsRecursive(this);

		if (defaultsPanel!=null) defaultsPanel.setLabels();
		if (euclidianPanel!=null) euclidianPanel.setLabels();
		if (euclidianPanel2!=null) euclidianPanel2.setLabels();
		if (spreadsheetPanel!=null) spreadsheetPanel.setLabels();
		if (casPanel!=null) casPanel.setLabels();
		if (advancedPanel!=null) advancedPanel.setLabels();
		if (objectPanel!=null) objectPanel.setLabels();
		if (layoutPanel!=null) layoutPanel.setLabels();
		
		updateStyleBar();
		styleBar.setLabels();
		updateTitleBar();
		

	}
	
	private void updateStyleBar(){

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}
	

	private void updateTitleBar(){
		((Application)app).getGuiManager().getLayout().getDockManager().getPanel(AbstractApplication.VIEW_PROPERTIES).updateTitleBar();
	}

	public void closeIfNotCurrentListener() {

		if (wrappedPanel != app.getCurrentSelectionListener()) {
			wrappedPanel.setVisible(false);
		}
	}

	// //////////////////////////////////////////////////////
	// VIEW INTERFACE
	// //////////////////////////////////////////////////////

	public void attachView() {
		if (attached){
			AbstractApplication.debug("already attached");
			return;
		}
		
		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		attached = true;
	}

	public void detachView() {
		kernel.detach(this);
		clearView();
		attached = false;
	}

	public void add(GeoElement geo) {
		objectPanel.add(geo);
		objectPanel.getTree().add(geo);

	}

	public void remove(GeoElement geo) {
		objectPanel.updateIfInSelection(geo);
		objectPanel.getTree().remove(geo);

	}

	public void rename(GeoElement geo) {
		objectPanel.rename(geo);
		objectPanel.getTree().rename(geo);
		updateTitleBar();

	}

	public void update(GeoElement geo) {

		// updateSelection();
		// propPanel.updateSelection(new GeoElement[] {geo});
		objectPanel.updateIfInSelection(geo);
		objectPanel.getTree().update(geo);

	}

	public void updateVisualStyle(GeoElement geo) {
		// update(geo);
		objectPanel.updateVisualStyle(geo);
		objectPanel.getTree().updateVisualStyle(geo);

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub
		objectPanel.updateIfInSelection(geo);
		objectPanel.getTree().updateAuxiliaryObject(geo);

	}
	

	public void repaintView() {
			
		if (objectPanel!=null){		
			if (app.getSelectedGeos()!=null && app.getSelectedGeos().size()==1)
				objectPanel.updateOneGeoDefinition(app.getSelectedGeos().get(0));
			
			objectPanel.getTree().repaint();
		}
		
		
	}

	public void reset() {
		objectPanel.getTree().repaint();

	}

	public void clearView() {
		objectPanel.getTree().clearView();
		

	}

	public void setMode(int mode) {
		// TODO Auto-generated method stub

	}

	public int getViewID() {
		return AbstractApplication.VIEW_PROPERTIES;
	}
	

	// //////////////////////////////////////////////////////
	// SELECTION
	// //////////////////////////////////////////////////////

	public void updateSelection() {

		updateSelection(removeAllConstants(app.getSelectedGeos()));
	}


	public void updateSelection(ArrayList<GeoElement> geos) {

		if (geos.size()>0){
			if (selectedOptionType!=OptionType.OBJECTS)
				setOptionPanel(OptionType.OBJECTS);

			objectPanel.updateSelection(geos);
			updateTitleBar(); 
			styleBar.setObjectsToolTip();
		}else{
			setOptionPanelRegardingFocus(true);
		}

	}
	
	
	private int selectedTab = 0;
	
	private void updateSelectedTab(Construction.Constants constant){
		switch (constant){
		case X_AXIS:
			selectedTab = 1;
			break;
		case Y_AXIS:
			selectedTab = 2;
			break;
		default:
			selectedTab = 0;
			break;
		}
	}

	private ArrayList<GeoElement> removeAllConstants(ArrayList<GeoElement> geosList){
		
		Construction.Constants firstRemovedConstant = Construction.Constants.NOT;
		
		
		//check if there is constants, remove it and remember what type
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		geos.addAll(geosList);
		for (int i = geos.size() - 1 ; i >= 0 ; i-- ) {
			GeoElement geo = geos.get(i);
			Construction.Constants constant = kernel.getConstruction().isConstantElement(geo);
			if (constant!=Construction.Constants.NOT){
				geos.remove(i);
				if (firstRemovedConstant==Construction.Constants.NOT)
					firstRemovedConstant=constant;
			}
		}
		
		updateSelectedTab(firstRemovedConstant);
		
		return geos;

	}



	// //////////////////////////////////////////////////////
	// FOR DOCK/UNDOCK PANEL
	// //////////////////////////////////////////////////////

	public void windowPanel() {
		objectPanel.setGeoTreeVisible();

		// kernel.attach(geoTree);
		// kernel.notifyAddAll(geoTree);
		// app.setSelectionListenerMode(this);
	}


	public void unwindowPanel() {
		objectPanel.setGeoTreeNotVisible();

		// kernel.detach(geoTree);
		// geoTree.clear();
		// app.setSelectionListenerMode(null);
	}


	public void closeDialog() {
		wrappedPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		app.storeUndoInfo();
		wrappedPanel.setCursor(Cursor.getDefaultCursor());
		app.getGuiManager().setShowView(false, getViewID());
	}

	
	public void showSliderTab() {
		objectPanel.showSliderTab();
	}

	public boolean hasFocus() {
		return wrappedPanel.hasFocus();
	}

	public JPanel getWrappedPanel() {
		// TODO Auto-generated method stub
		return wrappedPanel;
	}

}
