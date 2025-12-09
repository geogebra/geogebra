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
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.MaterialVisibility;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.BaseHeaderResizer;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.ToolbarDockPanelW;
import org.geogebra.web.full.gui.view.algebra.AVErrorHandler;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.MarblePanel;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.DefaultExportedApi;
import org.geogebra.web.html5.main.ExportedApi;
import org.geogebra.web.resources.SVGResource;

/**
 * General activity for all apps
 *
 * @author Zbynek
 */
public class BaseActivity implements GeoGebraActivity {

	private AppConfig config;
	private BaseHeaderResizer headerResizer;

	/**
	 * @param appConfig
	 *            config
	 */
	public BaseActivity(AppConfig appConfig) {
		this.config = appConfig;
	}

	@Override
	public AppConfig getConfig() {
		return this.config;
	}

	@Override
	public void start(AppW app) {
		// nothing to do
	}

	@Override
	public void initStylebar(DockPanelW dockPanelW) {
		dockPanelW.initGraphicsSettingsButton();
	}

	@Override
	public DockPanelW createAVPanel() {
		return new ToolbarDockPanelW(new DefaultDockPanelDecorator());
	}

	@Override
	public AlgebraItemHeader createAVItemHeader(RadioTreeItem radioTreeItem, boolean forInput) {
		return new MarblePanel(radioTreeItem, forInput);
	}

	@Override
	public ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders) {
		return new AVErrorHandler(radioTreeItem, valid, allowSliders, withSliders);
	}

	@Override
	public void showSettingsView(AppW app) {
		app.getDialogManager().showPropertiesDialog(OptionType.GLOBAL, null);
	}

	@Override
	public SVGResource getIcon() {
		// default implementation: classic and suite
		return MaterialDesignResources.INSTANCE.geogebra_color();
	}

	@Override
	public boolean useValidInput() {
		return true;
	}

	@Override
	public HeaderResizer getHeaderResizer(GeoGebraFrameW frame) {
		if (headerResizer == null) {
			headerResizer = new BaseHeaderResizer(frame);
		}
		return headerResizer;
	}

	@Override
	public boolean isWhiteboard() {
		return false;
	}

	@Override
	public ExportedApi getExportedApi() {
		return new DefaultExportedApi();
	}

	@Override
	public void markSearchOpen() {
		// nothing to do
	}

	@Override
	public void markSaveOpen() {
		// nothing to do
	}

	@Override
	public void markSaveProcess(String title, MaterialVisibility visibility) {
		// nothing to do
	}

	@Override
	public ScientificDataTableController getTableController() {
		return null;
	}
}
