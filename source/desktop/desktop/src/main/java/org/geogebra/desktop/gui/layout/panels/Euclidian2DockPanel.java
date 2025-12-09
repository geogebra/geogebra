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

package org.geogebra.desktop.gui.layout.panels;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Dock panel for the secondary euclidian view.
 */
public class Euclidian2DockPanel extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private AppD app;
	private int idx;

	/**
	 * @param app application
	 */
	public Euclidian2DockPanel(AppD app, String toolbar, int idx) {
		super(App.VIEW_EUCLIDIAN2, // view id
				"DrawingPad2", // view title phrase
				toolbar, // toolbar string
				true, // style bar?
				5, // menu order
				'2');
		this.idx = idx;
		this.app = app;
	}

	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) app.getEuclidianView2(1).getStyleBar();
	}

	@Override
	public EuclidianView getEuclidianView() {
		return app.getEuclidianView2(this.idx);
	}

	@Override
	public ImageIcon getIcon() {
		return app.getMenuIcon(GuiResourcesD.MENU_VIEW_GRAPHICS2);
	}

}
