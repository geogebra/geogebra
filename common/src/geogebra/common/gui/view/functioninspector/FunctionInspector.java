/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.gui.view.functioninspector;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.gui.view.functioninspector.FunctionInspectorModel.IFunctionInspectorListener;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;


/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public abstract class FunctionInspector implements View, UpdateFonts, SetLabels,
		IFunctionInspectorListener {

	private FunctionInspectorModel model;
	// ggb fields
	private Kernel kernel;

		private boolean isIniting;

	private boolean isChangingValue;

	private App app;

	/***************************************************************
	 * Constructs a FunctionInspecor
	 * 
	 * @param app
	 * @param selectedGeo
	 */
	public FunctionInspector(App app, GeoFunction selectedGeo) {

		this.setApp(app);
		setKernel(app.getKernel());
		app.getKernel().attach(this);

		setModel(new FunctionInspectorModel(app, selectedGeo, this));
		// create the GUI
		createGeoElementSlectionListener();
		createGUI();

		// load selected function
		insertGeoElement(selectedGeo);

		isIniting = false;

	}

	// ======================================================
	// GUI
	// ======================================================

	protected void createGUI() {

		// create the GUI components
		createGUIElements();
		createHeaderPanel();
		createTabPanel();
		
	}

	
	protected void createTabPanel() {

		createTabPointPanel();
		createTabIntervalPanel();
		buildTabPanel();
		
	}

	protected abstract void buildTabPanel();
	protected abstract void buildHelpPanel();
	protected abstract void buildHeaderPanel();

	protected void createHeaderPanel() {

		createHelpPanel();
		buildHeaderPanel();
	}

	private void createHelpPanel() {

		createOptionsButton();
		buildHelpPanel();
	}

	protected abstract void createTabIntervalPanel();
	protected abstract void createTabPointPanel();
	protected abstract void createGUIElements();
	protected void updateIntervalTab() {
		updateIntervalTable();
		getModel().updateIntervalGeoVisiblity();
	}
	
	protected abstract void updatePointsTab();
	
	protected abstract boolean isIntervalTabSelected();
	// =====================================
	// Update
	// =====================================

	public void updateGUI() {

		if (isIntervalTabSelected()) {
			updateIntervalTab();
	
		} else {
			updatePointsTab();
		}
		
		setLabels();
	}

	/**
	 * Updates the tab panels and thus the entire GUI. Also updates the active
	 * EV to hide/show temporary GeoElements associated with the
	 * FunctionInspector (e.g. points, integral)
	 */
	public void updateTabPanels() {
		if (isIntervalTabSelected()) {
			updateIntervalFields();
		} else {
			updatePointsTab();
		}
		getModel().updateGeos(isIntervalTabSelected());
		updateGUI();

	}

	protected abstract void updateIntervalFields();
	
	/**
	 * Updates the interval table. The max, min, roots, area etc. for the
	 * current interval are calculated and put into the IntervalTable model.
	 */
	protected void updateIntervalTable() {

		isChangingValue = true;
		getModel().updateIntervalTable();
		isChangingValue = false;

	}

	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	protected abstract void updateXYTable();
	protected abstract void removeColumn();
	
	public void setInspectorVisible(boolean isVisible) {
		if (isVisible) {
			App.debug("setInspectorVisible(true)");
				getApp().getKernel().attach(this);
			} else {
			App.debug("setInspectorVisible(false)");
			getApp().getKernel().detach(this);
			getModel().clearGeoList();
		}
	}

	// ====================================================
	// View Implementation
	// ====================================================

	public void update(GeoElement geo) {

		if (!getModel().isValid() || isChangingValue || isIniting) {
			return;
		}

		getModel().update(geo, !isIntervalTabSelected());

	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void add(GeoElement geo) {
	}

	public void remove(GeoElement geo) {
	}

	public void rename(GeoElement geo) {
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void repaintView() {
	}

	
	public void clearView() {
	}

	public void setMode(int mode, ModeSetter m) {
	}

	// ====================================================
	// Geo Selection Listener
	// ====================================================

	private void createGeoElementSlectionListener() {
		GeoElementSelectionListener sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo,
					boolean addToSelection) {
				insertGeoElement(geo);
			}
		};
		app.getSelectionManager().addSelectionListener(sl);
	}

	/**
	 * Sets the function to be inspected and updates the entire GUI
	 * 
	 * @param geo
	 *            The function to be inspected
	 */
	public void insertGeoElement(GeoElement geo) {
		if (geo == null || !geo.isGeoFunction()) {
			return;
		}
		getModel().insertGeoElement(geo);

		updateTabPanels();
	}

	protected void updateTestPoint() {

		if (isIniting) {
			return;
		}

		isChangingValue = true;

		getModel().updateTestPoint();

		isChangingValue = false;

	}


	protected void setStart(double x) {
		try {
			getModel().setStart(x);
			// Application.debug("" + start);
			updateXYTable();
			updateTestPoint();
		} catch (Exception e1) {
				e1.printStackTrace();
		}
		
	}
	
	protected abstract void changeStart(double x);
	
	
	public void changedNumberFormat() {
		this.updateGUI();
		this.updateIntervalFields();
		this.updateTestPoint();

	}

	protected abstract void createOptionsButton();
	protected abstract void doCopyToSpreadsheet();
	public int getViewID() {
		return App.VIEW_FUNCTION_INSPECTOR;
	}

	public void repaint() {
		App.debug("unimplemented");
	}

	public boolean isShowing() {
		App.debug("unimplemented");
		return false;
	}

	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public FunctionInspectorModel getModel() {
		return model;
	}

	public void setModel(FunctionInspectorModel model) {
		this.model = model;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

}
