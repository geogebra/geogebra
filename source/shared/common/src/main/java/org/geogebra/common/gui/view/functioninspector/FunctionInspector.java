/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.gui.view.functioninspector;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.view.functioninspector.FunctionInspectorModel.IFunctionInspectorListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.View;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GeoElementSelectionListener;
import org.geogebra.common.util.debug.Log;

/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public abstract class FunctionInspector
		implements View, UpdateFonts, SetLabels, IFunctionInspectorListener {

	private FunctionInspectorModel model;
	// ggb fields
	private Kernel kernel;

	private boolean isIniting;

	private boolean isChangingValue;

	protected App app;
	private GeoElementSelectionListener sl;

	/***************************************************************
	 * Constructs a FunctionInspecor
	 * 
	 * @param app
	 *            application
	 * @param selectedGeo
	 *            function
	 */
	public FunctionInspector(App app, GeoFunction selectedGeo) {
		this.setApp(app);
		setKernel(app.getKernel());
		app.getKernel().attach(this);

		setModel(new FunctionInspectorModel(app, selectedGeo, this));
		// create the GUI
		createGeoElementSelectionListener();
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
		getModel().updateIntervalGeoVisibility();
	}

	protected abstract void updatePointsTab();

	protected abstract boolean isIntervalTabSelected();
	// =====================================
	// Update
	// =====================================

	/**
	 * Update the UI
	 */
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

	/**
	 * @param isVisible
	 *            whether to show the FI
	 */
	public void setInspectorVisible(boolean isVisible) {
		if (isVisible) {
			Log.debug("setInspectorVisible(true)");
			app.getKernel().attach(this);
		} else {
			Log.debug("setInspectorVisible(false)");
			app.getKernel().detach(this);
			app.getSelectionManager().removeSelectionListener(sl);
			getModel().clearGeoList();
		}
	}

	// ====================================================
	// View Implementation
	// ====================================================

	@Override
	public void update(GeoElement geo) {
		if (!getModel().isValid() || isChangingValue || isIniting) {
			return;
		}

		getModel().update(geo, !isIntervalTabSelected());
	}

	@Override
	final public void updateVisualStyle(GeoElement geo, GProperty prop) {
		update(geo);
	}

	@Override
	public void add(GeoElement geo) {
		// not needed in this view
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not needed in this view
	}

	@Override
	public void remove(GeoElement geo) {
		// not needed in this view
	}

	@Override
	public void rename(GeoElement geo) {
		// not needed in this view
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not needed in this view
	}

	@Override
	public void repaintView() {
		// not needed in this view
	}

	@Override
	public void clearView() {
		// not needed in this view
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// not needed in this view
	}

	// ====================================================
	// Geo Selection Listener
	// ====================================================

	private void createGeoElementSelectionListener() {
		if (sl == null) {
			sl = new GeoElementSelectionListener() {
				@Override
				public void geoElementSelected(GeoElement geo,
						boolean addToSelection) {
					insertGeoElement(geo);
				}
			};
		}
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
			updateXYTable();
			updateTestPoint();
		} catch (Exception e1) {
			Log.debug(e1);
		}

	}

	protected abstract void changeStart(double x);

	@Override
	public void changedNumberFormat() {
		this.updateGUI();
		this.updateIntervalFields();
		this.updateTestPoint();

	}

	protected abstract void createOptionsButton();

	protected abstract void doCopyToSpreadsheet();

	@Override
	public int getViewID() {
		return App.VIEW_FUNCTION_INSPECTOR;
	}

	public void repaint() {
		//
	}

	@Override
	public void startBatchUpdate() {
		//
	}

	@Override
	public void endBatchUpdate() {
		//
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

	@Override
	public void updateHighlight(GeoElementND geo) {
		// nothing to do here
	}

}
