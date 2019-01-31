/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.view.properties;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.properties.PropertiesStyleBar;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.dialog.options.OptionPanelD;
import org.geogebra.desktop.gui.dialog.options.OptionsAdvancedD;
import org.geogebra.desktop.gui.dialog.options.OptionsAlgebraD;
import org.geogebra.desktop.gui.dialog.options.OptionsCASD;
import org.geogebra.desktop.gui.dialog.options.OptionsDefaultsD;
import org.geogebra.desktop.gui.dialog.options.OptionsEuclidianD;
import org.geogebra.desktop.gui.dialog.options.OptionsLayoutD;
import org.geogebra.desktop.gui.dialog.options.OptionsObjectD;
import org.geogebra.desktop.gui.dialog.options.OptionsSpreadsheetD;
import org.geogebra.desktop.gui.layout.DockManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.gui.layout.LayoutD;
import org.geogebra.desktop.gui.layout.panels.PropertiesDockPanel;
import org.geogebra.desktop.gui.view.spreadsheet.SpreadsheetViewD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * View for properties
 * 
 * @author mathieu
 */
public class PropertiesViewD extends PropertiesView implements SetLabels {

	// private GeoTree geoTree;

	private PropertiesStyleBarD styleBar;
	private JPanel wrappedPanel;

	// option panels
	private OptionsDefaultsD defaultsPanel;
	private OptionsEuclidianD<EuclidianViewD> euclidianPanel, euclidianPanel2;
	private OptionsSpreadsheetD spreadsheetPanel;
	private OptionsCASD casPanel;
	private OptionsAdvancedD advancedPanel;
	private OptionsLayoutD layoutPanel;
	private OptionsAlgebraD algebraPanel;

	private Object selectedOptionPanel = null;

	// GUI elements
	private JPanel mainPanel;
	// private JButton restoreDefaultsButton, saveButton;

	protected boolean isIniting = true;

	/**************************************************
	 * Constructor
	 * 
	 * @param app
	 */
	public PropertiesViewD(AppD app) {
		super(app);
		isIniting = true;
		this.wrappedPanel = new JPanel();

		app.setPropertiesView(this);

		// init object properties
		Log.debug("init object properties");
		app.setWaitCursor();
		getOptionPanel(OptionType.OBJECTS);
		Log.debug("end (init object properties)");

		// this.geoTree=geoTree;

		initGUI();
		setOptionPanel(selectedOptionType);
		isIniting = false;
		styleBar.getBtnOption().requestFocus();

		setLabels();

		app.setDefaultCursor();// remove this if init object properties is
								// faster
	}

	// ============================================
	// GUI
	// ============================================

	/** */
	public void initGUI() {

		wrappedPanel.setLayout(new BorderLayout());
		getStyleBar();

		mainPanel = new JPanel(new BorderLayout());
		wrappedPanel.add(mainPanel, BorderLayout.CENTER);
		wrappedPanel.add(Box.createVerticalGlue(), BorderLayout.SOUTH);

		// createButtonPanel();
		// add(buttonPanel, BorderLayout.SOUTH);

	}

	/*
	 * Creates the button panel for all option panels except the Object Panel
	 * 
	 * private void createButtonPanel() {
	 * 
	 * // panel with buttons at the bottom buttonPanel = new JPanel(new
	 * BorderLayout());
	 * 
	 * buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
	 * .createMatteBorder(1, 0, 0, 0, SystemColor.controlLtHighlight),
	 * BorderFactory.createEmptyBorder(5, 0, 5, 0)));
	 * 
	 * // buttonPanel.setBackground(Color.white);
	 * 
	 * // (restore defaults on the left side) JPanel panel = new JPanel(new
	 * FlowLayout(FlowLayout.LEFT)); // panel.setBackground(Color.white);
	 * 
	 * if (!app.isApplet()) { restoreDefaultsButton = new JButton();
	 * restoreDefaultsButton.addActionListener(new ActionListener() { public
	 * void actionPerformed(ActionEvent e) {
	 * GeoGebraPreferences.getPref().clearPreferences();
	 * 
	 * // reset defaults for GUI, views etc // this has to be called before load
	 * XML preferences, // in order to avoid overwrite
	 * app.getSettings().resetSettings();
	 * 
	 * // for geoelement defaults, this will do nothing, so it is // OK here
	 * GeoGebraPreferences.getPref().loadXMLPreferences(app);
	 * 
	 * // reset default line thickness etc
	 * app.getKernel().getConstruction().getConstructionDefaults()
	 * .resetDefaults();
	 * 
	 * // reset defaults for geoelements; this will create brand // new objects
	 * // so the options defaults dialog should be reset later
	 * app.getKernel().getConstruction().getConstructionDefaults()
	 * .createDefaultGeoElementsFromScratch();
	 * 
	 * // reset the stylebar defaultGeo if
	 * (app.getEuclidianView1().hasStyleBar())
	 * app.getEuclidianView1().getStyleBar() .restoreDefaultGeo(); if
	 * (app.hasEuclidianView2EitherShowingOrNot()) if
	 * (app.getEuclidianView2().hasStyleBar())
	 * app.getEuclidianView2().getStyleBar() .restoreDefaultGeo();
	 * 
	 * // restore dialog panels to display these defaults restoreDefaults();
	 * 
	 * } });
	 * 
	 * panel.add(restoreDefaultsButton); }
	 * 
	 * buttonPanel.add(panel, app.borderWest());
	 * 
	 * 
	 * // (save and close on the right side) panel = new JPanel(new
	 * FlowLayout(FlowLayout.RIGHT)); // panel.setBackground(Color.white);
	 * 
	 * 
	 * if (!app.isApplet()) { saveButton = new JButton();
	 * saveButton.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) {
	 * GeoGebraPreferences.getPref().saveXMLPreferences(app); } });
	 * panel.add(saveButton); }
	 * 
	 * 
	 * buttonPanel.add(panel, app.borderEast());
	 * 
	 * 
	 * }
	 */

	/**
	 * Restores default settings in option dialogs
	 */
	public void restoreDefaults() {

		((OptionsDefaultsD) getOptionPanel(OptionType.DEFAULTS))
				.restoreDefaults();
		((OptionsAdvancedD) getOptionPanel(OptionType.GLOBAL))
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
	 * view is shown. Called when a view gets the focus.
	 */
	@Override
	public void updatePropertiesView() {

		if (!isShowing()) {
			return;
		}

		setOptionPanelRegardingFocus(false);
	}

	/**
	 * acts when mouse has been pressed in euclidian controller
	 */
	@Override
	public void mousePressedForPropertiesView() {
		getObjectPanel().forgetGeoAdded();
	}

	/**
	 * apply current panel modifications
	 */
	public void applyModifications() {

		if (selectedOptionPanel != null) {
			((OptionPanelD) selectedOptionPanel).applyModifications();
		}

	}

	/**
	 * set the current panel selected/unselected
	 * 
	 * @param isVisible
	 *            visible
	 */
	public void setSelectedOptionPanelVisible(boolean isVisible) {

		if (selectedOptionPanel != null) {
			((OptionPanelD) selectedOptionPanel).setSelected(isVisible);
		}

	}

	@Override
	protected void setObjectsToolTip() {
		styleBar.setObjectsToolTip();
	}

	@Override
	protected void setOptionPanelWithoutCheck(OptionType type) {

		// App.printStacktrace("\ntype="+type);

		if (selectedOptionType != type && selectedOptionPanel != null) {
			((OptionPanelD) selectedOptionPanel).applyModifications();
			((OptionPanelD) selectedOptionPanel).setSelected(false);
		}

		if (!isIniting && selectedOptionType == type
				&& type != OptionType.EUCLIDIAN_FOR_PLANE) {
			updateTitleBar();
			return;
		}

		selectedOptionType = type;

		// clear the center panel and replace with selected option panel

		mainPanel.removeAll();
		selectedOptionPanel = getOptionPanel(type);

		mainPanel.add(getOptionPanel(type).getWrappedPanel(),
				BorderLayout.CENTER);

		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		((OptionPanelD) selectedOptionPanel).setSelected(true);
		((OptionPanelD) selectedOptionPanel).updateGUI();
		((OptionPanelD) selectedOptionPanel).revalidate();
		updateStyleBar();
		updateTitleBar();

		wrappedPanel.revalidate();
		wrappedPanel.repaint();
	}

	public void updateGUI() {

		if (defaultsPanel != null) {
			defaultsPanel.updateGUI();
		}

		updateEuclidianPanelsGUI();

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
		if (getObjectPanel() != null) {
			((OptionsObjectD) getObjectPanel())
					.setVisible(selectedOptionType == OptionType.OBJECTS);
		}

		setLabels();

	}

	/**
	 * update euclidian panels GUI
	 */
	public void updateEuclidianPanelsGUI() {
		if (euclidianPanel != null) {
			euclidianPanel.updateGUI();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.updateGUI();
		}
	}

	/**
	 * update panel GUI
	 * 
	 * @param id
	 *            view id
	 */
	public void updatePanelGUI(int id) {
		switch (id) {
		default:
			// do nothing
			break;
		case App.VIEW_EUCLIDIAN:
			if (euclidianPanel != null) {
				euclidianPanel.updateGUI();
			}
			break;
		case App.VIEW_EUCLIDIAN2:
			if (euclidianPanel2 != null) {
				euclidianPanel2.updateGUI();
			}
			break;
		case App.VIEW_SPREADSHEET:
			if (spreadsheetPanel != null) {
				spreadsheetPanel.updateGUI();
			}
			break;
		case App.VIEW_CAS:
			if (casPanel != null) {
				casPanel.updateGUI();
			}
			break;
		}
	}

	/**
	 * Returns the option panel for the given type. If the panel does not exist,
	 * a new one is constructed
	 * 
	 * @param type
	 * @return
	 */
	public OptionPanelD getOptionPanel(OptionType type) {

		// AbstractApplication.printStacktrace("type :"+type);

		switch (type) {
		case DEFAULTS:
			if (defaultsPanel == null) {
				defaultsPanel = new OptionsDefaultsD((AppD) app);
			}
			return defaultsPanel;

		case CAS:
			if (casPanel == null) {
				casPanel = new OptionsCASD((AppD) app);
			}
			return casPanel;

		case EUCLIDIAN:
			if (euclidianPanel == null) {
				euclidianPanel = new OptionsEuclidianD<>(
						(AppD) app,
						((AppD) app).getEuclidianView1());
				euclidianPanel.setLabels();
				// euclidianPanel.setView(((AppD) app).getEuclidianView1());
			}

			return euclidianPanel;

		case EUCLIDIAN2:
			if (euclidianPanel2 == null) {
				euclidianPanel2 = new OptionsEuclidianD<>((AppD) app,
						((AppD) app).getEuclidianView2(1));
				euclidianPanel2.setLabels();
				// euclidianPanel2.setView(((AppD) app).getEuclidianView2());
			}

			return euclidianPanel2;

		case SPREADSHEET:
			if (spreadsheetPanel == null) {
				spreadsheetPanel = new OptionsSpreadsheetD((AppD) app,
						(SpreadsheetViewD) app.getGuiManager()
								.getSpreadsheetView());
			}
			return spreadsheetPanel;

		case GLOBAL:
			if (advancedPanel == null) {
				advancedPanel = new OptionsAdvancedD((AppD) app);
			}
			return advancedPanel;

		case LAYOUT:
			if (layoutPanel == null) {
				layoutPanel = new OptionsLayoutD((AppD) app);
			}
			return layoutPanel;

		case OBJECTS:
			if (getObjectPanel() == null) {
				setObjectPanel(new OptionsObjectD((AppD) app));
				((OptionsObjectD) getObjectPanel()).setMinimumSize(
						((OptionsObjectD) getObjectPanel()).getPreferredSize());
			}
			return (OptionPanelD) getObjectPanel();
		case ALGEBRA:
			if (algebraPanel == null) {
				algebraPanel = new OptionsAlgebraD((AppD) app);
			}
			return algebraPanel;
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
	protected PropertiesStyleBarD newPropertiesStyleBar() {
		return new PropertiesStyleBarD(this, (AppD) app);
	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	@Override
	public void setLabels() {

		if (defaultsPanel != null) {
			defaultsPanel.setLabels();
		}
		if (euclidianPanel != null) {
			euclidianPanel.setLabels();
		}
		if (euclidianPanel2 != null) {
			euclidianPanel2.setLabels();
		}
		if (spreadsheetPanel != null) {
			spreadsheetPanel.setLabels();
		}
		if (casPanel != null) {
			casPanel.setLabels();
		}
		if (advancedPanel != null) {
			advancedPanel.setLabels();
		}
		if (getObjectPanel() != null) {
			((SetLabels) getObjectPanel()).setLabels();
		}
		if (layoutPanel != null) {
			layoutPanel.setLabels();
		}

		updateStyleBar();
		styleBar.setLabels();
		updateTitleBar();

	}

	@Override
	public void updateStyleBar() {

		if (styleBar != null) {
			styleBar.updateGUI();
		}
	}

	@Override
	protected void updateTitleBar() {
		((LayoutD) app.getGuiManager().getLayout()).getDockManager()
				.getPanel(App.VIEW_PROPERTIES).updateTitleBar();
	}

	// //////////////////////////////////////////////////////
	// VIEW INTERFACE
	// //////////////////////////////////////////////////////

	@Override
	public void attachView() {
		if (isAttached()) {
			Log.debug("already attached");
			return;
		}

		clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);
		setAttached(true);
	}

	@Override
	public void detachView() {
		kernel.detach(this);
		clearView();
		setAttached(false);
	}

	@Override
	public void add(GeoElement geo) {
		getObjectPanel().add(geo);
		((OptionsObjectD) getObjectPanel()).getTree().add(geo);
		styleBar.setObjectButtonEnable(true);

	}

	@Override
	public void remove(GeoElement geo) {
		// ((OptionsObjectD) objectPanel).updateIfInSelection(geo);
		((OptionsObjectD) getObjectPanel()).getTree().remove(geo);
		if (app.getKernel().isEmpty()) {
			styleBar.setObjectButtonEnable(false);
		}

	}

	@Override
	public void rename(GeoElement geo) {
		if (!isShowing()) {
			return;
		}

		((OptionsObjectD) getObjectPanel()).rename(geo);
		((OptionsObjectD) getObjectPanel()).getTree().rename(geo);
		updateTitleBar();

	}

	@Override
	public void update(GeoElement geo) {

		if (!isShowing() || (!geo.isLabelSet() && !geo.isGeoCasCell())) {
			return;
		}

		// updateSelection();
		// propPanel.updateSelection(new GeoElement[] {geo});
		((OptionsObjectD) getObjectPanel()).updateIfInSelection(geo);
		((OptionsObjectD) getObjectPanel()).getTree().update(geo);

	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {

		if (!isShowing() || (!geo.isLabelSet() && !geo.isGeoCasCell())) {
			return;
		}

		((OptionsObjectD) getObjectPanel()).updateVisualStyle(geo);
		((OptionsObjectD) getObjectPanel()).getTree().updateVisualStyle(geo, prop);

	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {

		if (!isShowing()) {
			return;
		}

		((OptionsObjectD) getObjectPanel()).updateIfInSelection(geo);
		((OptionsObjectD) getObjectPanel()).getTree().updateAuxiliaryObject(geo);

	}

	@Override
	public void repaintView() {

		if (!isShowing()) {
			return;
		}

		if (getObjectPanel() != null) {
			((OptionsObjectD) getObjectPanel()).getTree().repaint();
		}

	}

	@Override
	public void reset() {
		((OptionsObjectD) getObjectPanel()).getTree().repaint();

	}

	@Override
	public void clearView() {
		((OptionsObjectD) getObjectPanel()).getTree().clearView();

	}

	private int mode = EuclidianConstants.MODE_MOVE;

	@Override
	public void setMode(int mode, ModeSetter m) {

		// on init, mode=-1
		if (mode < 0) {
			return;
		}

		if (mode == this.mode) {
			return;
		}

		// close undocked properties view when setting mode
		// if propreties view covers a part of the main window
		DockManagerD manager = ((LayoutD) app.getGuiManager().getLayout())
				.getDockManager();
		DockPanelD panel = manager.getPanel(getViewID());
		if (panel.isInFrame()) {
			Rectangle panelRectangle = panel.getFrameBounds();
			Rectangle mainWindowRectangle = ((AppD) app).getMainComponent()
					.getBounds();

			boolean outside = (panelRectangle.x > mainWindowRectangle.x
					+ mainWindowRectangle.width)
					|| (panelRectangle.x
							+ panelRectangle.width < mainWindowRectangle.x)
					|| (panelRectangle.y > mainWindowRectangle.y
							+ mainWindowRectangle.height)
					|| (panelRectangle.y
							+ panelRectangle.height < mainWindowRectangle.y);

			if (!outside) {
				manager.closePanel(panel, false);
			}
		}

		this.mode = mode;

	}

	@Override
	public int getViewID() {
		return App.VIEW_PROPERTIES;
	}

	// //////////////////////////////////////////////////////
	// SELECTION
	// //////////////////////////////////////////////////////

	@Override
	public void updateSelection() {

		if (!isShowing()) {
			return;
		}

		ArrayList<GeoElement> geos = app.getSelectionManager()
				.getSelectedGeos();

		if (geos.size() > 0) {
			updateSelection(removeAllConstants(geos));
		}
	}

	@Override
	public void updateSelection(ArrayList<GeoElement> geos) {

		if (geos.size() > 0) {
			if (!stayInCurrentPanel()) {
				setObjectPanel(geos);
			}
		} else {
			setOptionPanelRegardingFocus(true);
		}

	}

	private void setObjectPanel(ArrayList<GeoElement> geos) {

		if (geos.size() == 0) {
			app.getSelectionManager().setFirstGeoSelectedForPropertiesView();

			GeoElement geo = app.getSelectionManager()
					.setFirstGeoSelectedForPropertiesView();
			if (geo == null) {
				// if no first geo, close properties view if object panel
				// visible
				if (selectedOptionType == OptionType.OBJECTS) {
					((LayoutD) app.getGuiManager().getLayout()).getDockManager()
							.closePanel(getViewID(), false);
				}

			} else if (selectedOptionType != OptionType.OBJECTS) {
				setOptionPanel(OptionType.OBJECTS);
			}
		} else if (selectedOptionType != OptionType.OBJECTS) {
			setOptionPanel(OptionType.OBJECTS);
		}

		// always update selection for object panel
		updateObjectPanelSelection(geos);
	}

	@Override
	protected void updateObjectPanelSelection(ArrayList<GeoElement> geos) {
		((OptionsObjectD) getObjectPanel()).updateSelection(geos);
		updateTitleBar();
		styleBar.setObjectsToolTip();
	}

	@Override
	protected void setSelectedTab(OptionType type) {
		switch (type) {
		default:
			// do nothing
			break;
		case EUCLIDIAN:
			euclidianPanel.setSelectedTab(getSelectedTab());
			break;
		case EUCLIDIAN2:
			euclidianPanel2.setSelectedTab(getSelectedTab());
			break;
		}
	}

	// //////////////////////////////////////////////////////
	// FOR DOCK/UNDOCK PANEL
	// //////////////////////////////////////////////////////

	public void windowPanel() {
		applyModifications();
		((OptionsObjectD) getObjectPanel()).setGeoTreeVisible();

		// kernel.attach(geoTree);
		// kernel.notifyAddAll(geoTree);
		// app.setSelectionListenerMode(this);
	}

	public void unwindowPanel() {
		applyModifications();
		((OptionsObjectD) getObjectPanel()).setGeoTreeNotVisible();

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
		selectedOptionType = OptionType.EUCLIDIAN;
		setOptionPanel(OptionType.OBJECTS);
		((OptionsObjectD) getObjectPanel()).showSliderTab();
		styleBar.updateGUI();
		updateGUI();
	}

	@Override
	public boolean hasFocus() {
		return wrappedPanel.hasFocus();
	}

	public JPanel getWrappedPanel() {
		// TODO Auto-generated method stub
		return wrappedPanel;
	}

	/**
	 * @param app
	 * @param type
	 * @return
	 */
	public static ImageIcon getTypeIcon(AppD app, OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getScaledIcon(GuiResourcesD.PROPERTIES_DEFAULTS_3);
		case SPREADSHEET:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_SPREADSHEET);
		case ALGEBRA:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_ALGEBRA);
		case EUCLIDIAN:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_GRAPHICS);
		case EUCLIDIAN2:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_GRAPHICS2);
		case CAS:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_CAS);
		case GLOBAL:
			return app.getScaledIcon(GuiResourcesD.OPTIONS_ADVANCED_24);
		case OBJECTS:
			return app.getScaledIcon(GuiResourcesD.OPTIONS_OBJECTS_24);
		case LAYOUT:
			return app.getScaledIcon(GuiResourcesD.OPTIONS_LAYOUT_24);
		case EUCLIDIAN3D:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_GRAPHICS3D);
		case EUCLIDIAN_FOR_PLANE:
			return app.getScaledIcon(GuiResourcesD.MENU_VIEW_GRAPHICS_EXTRA);
		}
		return null;
	}

	/**
	 * update fonts
	 */
	public void updateFonts() {

		if (isIniting) {
			return;
		}

		Font font = ((AppD) app).getPlainFont();
		mainPanel.setFont(font);

		if (defaultsPanel != null)
		 {
			defaultsPanel.updateFont(); // tree + button
		}
		if (euclidianPanel != null)
		 {
			euclidianPanel.updateFont(); //
		}
		if (euclidianPanel2 != null)
		 {
			euclidianPanel2.updateFont(); //
		}
		if (spreadsheetPanel != null)
		 {
			spreadsheetPanel.updateFont(); //
		}
		if (casPanel != null)
		 {
			casPanel.updateFont(); //
		}
		if (advancedPanel != null)
		 {
			advancedPanel.updateFont(); //
		}
		if (getObjectPanel() != null)
		 {
			((OptionsObjectD) getObjectPanel()).updateFont(); // tree
		}
		if (layoutPanel != null)
		 {
			layoutPanel.updateFont(); //
		}

		if (styleBar != null) {
			styleBar.reinit();
		}
		// SwingUtilities.updateComponentTreeUI(mainPanel);

	}

	public void repaint() {
		Log.debug("unimplemented");
	}

	public boolean isShowing() {

		PropertiesDockPanel dockPanel = ((GuiManagerD) app.getGuiManager())
				.getPropertiesDockPanel();
		if (dockPanel == null) {
			return false;
		}

		return dockPanel.isVisible();
	}

	/**
	 * @param geo
	 *            GeoText to be updated
	 */
	public void updateTextEditor(GeoElement geo) {
		if (getObjectPanel() != null) {
			((OptionsObjectD) getObjectPanel()).updateTextEditor(geo);
		}
	}

	@Override
	public void setOptionPanel(OptionType type, int subType) {
		setOptionPanel(type);
	}

	@Override
	public boolean suggestRepaint() {
		return false;
		// only for web
	}

}
