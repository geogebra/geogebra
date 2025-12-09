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

package org.geogebra.web.geogebra3D.web.gui.layout.panels;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelWAbstract;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.ContextMenuGraphicsWindow3DW;
import org.geogebra.web.html5.Browser;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Dock panel for 3D
 */
public class EuclidianDockPanel3DW extends EuclidianDockPanelWAbstract {
	/**
	 * default width of this panel
	 */
	public static final int DEFAULT_WIDTH = 480;
	/** the inner panel */
	EuclidianPanel euclidianpanel;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * 
	 */
	public EuclidianDockPanel3DW(AppWFull app) {
		super(App.VIEW_EUCLIDIAN3D, // view id
				ToolBar.getAllToolsNoMacros3D(app), // toolbar string
				true, // style bar?
				true); // zoom panel
		this.app = app;
		this.setEmbeddedSize(DEFAULT_WIDTH);
	}

	@Override
	protected Widget loadComponent() {
		// 2D app or exam: just flow panel; 3D app in old browser: EVnoWebGL
		if (!app.supportsView(App.VIEW_EUCLIDIAN3D)
				&& Browser.supportsWebGL()) {
			return new FlowPanel();
		}
		EuclidianView3DW view = (EuclidianView3DW) app.getEuclidianView3D();
		euclidianpanel = new EuclidianPanel(this,
				(AbsolutePanel) view.getComponent());
		return euclidianpanel;
	}

	@Override
	protected Widget loadStyleBar() {
		if (getEuclidianView() == null) {
			return super.loadStyleBar();
		}
		return (Widget) getEuclidianView().getStyleBar();
	}

	@Override
	protected ContextMenuGraphicsWindow3DW getGraphicsWindowContextMenu() {
		return new ContextMenuGraphicsWindow3DW(app);
	}

	@Override
	public EuclidianView3DW getEuclidianView() {
		// do NOT initialize the view if it wasn't done previously
		if (app != null && app.isEuclidianView3Dinited()) {
			return (EuclidianView3DW) app.getEuclidianView3D();
		}
		return null;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	@Override
	public void calculateEnvironment() {
		if (app.isEuclidianView3Dinited()) {
			app.getEuclidianView3D().getEuclidianController()
					.calculateEnvironment();
		}
	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsView3DDimChanged(width, height);
	}

	@Override
	protected boolean needsResetIcon() {
		return app.showResetIcon() && !app.showView(App.VIEW_EUCLIDIAN)
				&& !app.showView(App.VIEW_EUCLIDIAN2);
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_graphics3dView();
	}

}
