/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.main.activity;

import org.geogebra.common.gui.view.table.ScientificDataTableController;
import org.geogebra.common.gui.view.table.TableValues;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.resources.SVGResource;

/**
 * App-specific behaviors
 */
public interface GeoGebraActivity {

	/**
	 * @return application configuration
	 */
	AppConfig getConfig();

	/**
	 * Initialize the activity
	 *
	 * @param app
	 *            app
	 */
	void start(AppW app);

	/**
	 * Build title bar for a dock panel.
	 *
	 * @param dockPanelW
	 *            dock panel
	 */
	void initStylebar(DockPanelW dockPanelW);

	/**
	 * @return panel for algebra view
	 */
	DockPanelW createAVPanel();

	/**
	 * @param radioTreeItem
	 *            AV item
	 * @return header for AV item
	 */
	AlgebraItemHeader createAVItemHeader(RadioTreeItem radioTreeItem, boolean forInput);

	/**
	 * @param radioTreeItem parent item
	 * @param valid         previous input valid
	 * @param allowSliders  whether to allow sliders at all
	 * @param withSliders   whether to allow slider creation without asking
	 * @return error handler for algebra input.
	 */
	ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders);

	/**
	 * Show settings
	 *
	 * @param app
	 *            application
	 */
	void showSettingsView(AppW app);

	/**
	 * @return icon for menu
	 */
	SVGResource getIcon();

	/**
	 *
	 * @return use valid input
	 */
	boolean useValidInput();

	/**
	 * @param frame application frame
	 * @return resizer class for the external header.
	 */
	HeaderResizer getHeaderResizer(GeoGebraFrameW frame);

	/**
	 * Please try to avoid if(isWhiteboard), use polymorphism instead
	 * 
	 * @return whether this is whiteboard activity
	 */
	boolean isWhiteboard();

	/**
	 * @return API for integration with external websites
	 */
	ExportedApi getExportedApi();

	/**
	 * mark search view was open before login
	 */
	void markSearchOpen();

	/**
	 * mark save dialog was open before login
	 */
	void markSaveOpen();

	/**
	 * mark saving process
	 * @param title material title
	 * @param visibility material visibility
	 */
	void markSaveProcess(String title, MaterialVisibility visibility);

	/**
	 * @return data table controller for SciCalc, null otherwise
	 */
	ScientificDataTableController getTableController();

	/**
	 * Create default functions for table of values and/or make the table editable
	 * @param app application
	 */
	default void initTableOfValues(AppW app) {
		TableValues tableValues =
				((GuiManagerW) app.getGuiManager()).getTableValuesViewOrNull();
		if (tableValues != null) {
			tableValues.getTableValuesModel().setAllowsAddingColumns(true);
			((TableValuesView) tableValues).setAlgebraLabelVisibleCheck(true);
		}
	}

	default GeoGebraActivity getSubapp() {
		return this;
	}
}
