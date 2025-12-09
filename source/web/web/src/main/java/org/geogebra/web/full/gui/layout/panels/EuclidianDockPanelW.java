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

package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract
		implements EuclidianPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null; // static foreground

	/**
	 * This constructor is used by the applet
	 * 
	 * @param application
	 *            application
	 * @param stylebar
	 *            whether to use stylebar
	 */
	public EuclidianDockPanelW(AppWFull application, boolean stylebar) {
		super(App.VIEW_EUCLIDIAN, null, stylebar, true);

		// TODO: run loadComponent later like for other panels (check if it works in all applets)
		app = application;
		component = loadComponent();
		if (!stylebar) {
			buildDockPanel();
		}
		initNavigationBar();
	}

	private void initNavigationBar() {
		// GuiManager can be null at the startup of the application,
		// but then the addNavigationBar method will be called explicitly.
		if (app.getGuiManager() != null
				&& app.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			addNavigationBar();
		}
	}

	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			TestHarness.setAttr(eview1, "euclidianView");
			addCanvas(eview1);
		}
		return euclidianpanel;
	}

	private void addCanvas(Canvas c) {
		if (c != null) {
			euclidianpanel.getAbsolutePanel().add(c);
		}
	}

	@Override
	protected Widget loadStyleBar() {
		if (espanel == null) {
			espanel = app.getEuclidianView1().getStyleBar();
		}
		return (Widget) espanel;
	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	/**
	 * @param w
	 *            widget to be removed
	 */
	public void remove(Widget w) {
		euclidianpanel.remove(w);
	}

	@Override
	public EuclidianView getEuclidianView() {
		if (app != null) {
			return app.getEuclidianView1();
		}
		return null;
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics();
	}

	@Override
	public void calculateEnvironment() {
		app.getEuclidianView1().getEuclidianController().calculateEnvironment();
	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsViewDimChanged(width, height);
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_graphicsView();
	}
}
