/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.properties;

import geogebra.common.euclidian.EuclidianConstants;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.gui.GuiManager;
import geogebra.gui.dialog.options.OptionsAdvanced;
import geogebra.gui.dialog.options.OptionsCAS;
import geogebra.gui.dialog.options.OptionsDefaults;
import geogebra.gui.dialog.options.OptionsEuclidian;
import geogebra.gui.dialog.options.OptionsLayout;
import geogebra.gui.dialog.options.OptionsObject;
import geogebra.gui.dialog.options.OptionsSpreadsheet;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * View for properties
 * 
 * @author mathieu
 * @version
 */
public class PropertiesView extends JPanel implements
		geogebra.common.gui.view.properties.PropertiesView, WindowFocusListener {

	private static final long serialVersionUID = 1L;

	// private JTreeGeoElements geoTree;

	private Application app;
	private boolean attached;

	private PropertiesStyleBar styleBar;

	private OptionsDefaults defaultsPanel;
	private OptionsEuclidian euclidianPanel;
	private OptionsSpreadsheet spreadsheetPanel;
	private OptionsCAS casPanel;
	private OptionsAdvanced advancedPanel;
	// private PropertiesPanel propPanel;
	private OptionsObject objectPanel;
	private OptionsLayout layoutPanel;

	private boolean isIniting = true;

	public enum OptionType {
		// Order matters for the selection menu. A separator is placed after
		// OBJECTS and SPREADSHEET to isolate the view options
		OBJECTS, EUCLIDIAN, EUCLIDIAN2, CAS, SPREADSHEET, LAYOUT, DEFAULTS, ADVANCED
	}

	private OptionType selectedOptionType = OptionType.LAYOUT;

	private JPanel mainPanel;

	public OptionType getSelectedOptionType() {
		return selectedOptionType;
	}

	public PropertiesView(Application app) {

		super();

		this.app = app;
		app.setPropertiesView(this);
		// this.geoTree=geoTree;

		initGUI();
		setOptionPanel(selectedOptionType);
		isIniting = false;
		styleBar.getBtnOption().requestFocus();
	}

	/** */
	public void initGUI() {

		setLayout(new BorderLayout());
		add(getStyleBar(), BorderLayout.NORTH);
		mainPanel = new JPanel(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
	}

	public void updatePropertiesView() {

		if (app.getSelectedGeos().size() > 0) {
			setOptionPanel(OptionType.OBJECTS);
		} else {
			int focusedView = app.getGuiManager().getLayout().getDockManager()
					.getFocusedViewId();
			setOptionPanelByView(focusedView);
		}
	}

	/**
	 * Sets and shows the option panel for the given view id
	 * 
	 * @param type
	 */
	public void setOptionPanelByView(int viewID) {

		HashMap<Integer, OptionType> viewMap = new HashMap<Integer, OptionType>();
		viewMap.put(AbstractApplication.VIEW_CAS, OptionType.CAS);
		viewMap.put(AbstractApplication.VIEW_SPREADSHEET,
				OptionType.SPREADSHEET);
		viewMap.put(AbstractApplication.VIEW_EUCLIDIAN, OptionType.EUCLIDIAN);
		viewMap.put(AbstractApplication.VIEW_EUCLIDIAN2, OptionType.EUCLIDIAN2);

		setOptionPanel(viewMap.get(viewID));
	}

	/**
	 * Sets and shows the option panel for the given option type
	 * 
	 * @param type
	 */
	public void setOptionPanel(OptionType type) {

		if (type == null || !isIniting && selectedOptionType == type) {
			return;
		}

		selectedOptionType = type;

		// clear the center panel and replace with selected option panel
		mainPanel.removeAll();
		getOptionPanel(type).setBorder(
				BorderFactory.createEmptyBorder(15, 10, 10, 10));
		mainPanel.add(getOptionPanel(type), BorderLayout.CENTER);

		// update GUI
		updateGUI();

		getOptionPanel(type).revalidate();
		this.revalidate();
		this.repaint();
	}

	public void updateGUI() {
		if (defaultsPanel != null) {
			defaultsPanel.updateGUI();
		}
		if (euclidianPanel != null) {
			euclidianPanel.updateGUI();
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

		if (styleBar != null) {
			styleBar.updateGUI();
		}

			app.getGuiManager().getLayout().getDockManager().getPanel(AbstractApplication.VIEW_PROPERTIES).updateTitleBar();
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 * @return
	 */
	public JComponent getOptionPanel(OptionType type) {

		switch (type) {
		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaults(app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCAS(app);
				casPanel.updateGUI();
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidian(app,
						app.getActiveEuclidianView());
			}
			euclidianPanel.setLabels();
			euclidianPanel.setView(app.getEuclidianView1());
			euclidianPanel.showCbView(false);
			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidian(app,
						app.getEuclidianView2());
			}
			euclidianPanel.setLabels();
			euclidianPanel.setView(app.getEuclidianView2());
			euclidianPanel.showCbView(false);
			return euclidianPanel;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheet(app, app
						.getGuiManager().getSpreadsheetView());
			}
			return spreadsheetPanel;

		case ADVANCED:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvanced(app);
			}
			return advancedPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayout(app);
			}
			return layoutPanel;

		case OBJECTS:
			if (objectPanel == null) {
				objectPanel = new OptionsObject(app);
				objectPanel.setMinimumSize(objectPanel.getPreferredSize());
			}
			return objectPanel;
		}
		return null;
	}

	public String getTypeString(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getPlain("Defaults");
		case SPREADSHEET:
			return app.getPlain("Spreadsheet");
		case EUCLIDIAN:
			return app.getPlain("DrawingPad");
		case EUCLIDIAN2:
			return app.getPlain("DrawingPad2");
		case CAS:
			return app.getMenu("CAS");
		case ADVANCED:
			return app.getMenu("Advanced");
		case OBJECTS:
			return app.getMenu("Objects");
		case LAYOUT:
			return app.getMenu("Layout");
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
		return new PropertiesStyleBar(this, app);
	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {

		GuiManager.setLabelsRecursive(this);
	}

	public void closeIfNotCurrentListener() {

		if (this != app.getCurrentSelectionListener()) {
			this.setVisible(false);
		}
	}

	// //////////////////////////////////////////////////////
	// VIEW INTERFACE
	// //////////////////////////////////////////////////////

	public void attachView() {
		clearView();
		// kernel.notifyAddAll(this);
		// kernel.attach(this);
		attached = true;
	}

	public void detachView() {
		// kernel.detach(this);
		clearView();
		attached = false;
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

		// updateSelection();
		// propPanel.updateSelection(new GeoElement[] {geo});

	}

	public void updateVisualStyle(GeoElement geo) {
		// update(geo);

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// propPanel.updateSelection(app.getSelectedGeos().toArray());
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
		return AbstractApplication.VIEW_PROPERTIES;
	}

	// //////////////////////////////////////////////////////
	// SELECTION
	// //////////////////////////////////////////////////////

	public void updateSelection() {

		if (app.getSelectedGeos().toArray().length > 0) {
			// propPanel.updateSelection(app.getSelectedGeos().toArray());
			this.setVisible(true);
		} else {
			// this.setVisible(false);
		}
	}

	// private ArrayList<GeoElement> tempArrayList = new
	// ArrayList<GeoElement>();

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		if (objectPanel != null) {
			objectPanel.geoElementSelected(geo, addToSelection);
		}

	}

	public void selectionChanged() {
		if (objectPanel != null) {
			objectPanel.selectionChanged();
		}
	}

	// //////////////////////////////////////////////////////
	// FOR DOCK/UNDOCK PANEL
	// //////////////////////////////////////////////////////

	public void windowPanel() {
		// setGeoTreeVisible();

		// kernel.attach(geoTree);
		// kernel.notifyAddAll(geoTree);
		// app.setSelectionListenerMode(this);
	}

	public void unwindowPanel() {
		// setGeoTreeNotVisible();

		// kernel.detach(geoTree);
		// geoTree.clear();
		// app.setSelectionListenerMode(null);
	}

	public void closeDialog() {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
	}

	public void windowGainedFocus(WindowEvent arg0) {
		System.out.println("gained focus");
		// make sure this dialog is the current selection listener
		if (app.getMode() != EuclidianConstants.MODE_SELECTION_LISTENER
				|| app.getCurrentSelectionListener() != this) {
			// TODO
			app.setSelectionListenerMode(this);
			selectionChanged();
		}
	}

	public void windowLostFocus(WindowEvent arg0) {
		System.out.println("lost focus");
	}

}
