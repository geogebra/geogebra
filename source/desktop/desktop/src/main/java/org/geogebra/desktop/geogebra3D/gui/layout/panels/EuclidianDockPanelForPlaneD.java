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

package org.geogebra.desktop.geogebra3D.gui.layout.panels;

import javax.swing.JComponent;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.desktop.awt.GRectangleD;
import org.geogebra.desktop.geogebra3D.euclidianForPlane.EuclidianViewForPlaneD;
import org.geogebra.desktop.gui.layout.panels.EuclidianDockPanelAbstract;
import org.geogebra.desktop.main.AppD;

/**
 * Dock panel for the primary euclidian view.
 */
public class EuclidianDockPanelForPlaneD extends EuclidianDockPanelAbstract {
	private static final long serialVersionUID = 1L;
	private EuclidianViewForPlaneD view;

	/**
	 * @param app
	 *            application
	 * @param view
	 *            view for plane
	 */
	public EuclidianDockPanelForPlaneD(AppD app, EuclidianViewForPlaneD view,
			int viewId) {
		super(viewId, // view id
				"GraphicsViewForPlaneA", // view title
				ToolBar.getAllToolsNoMacrosForPlane(), // toolbar string
				true, // style bar?
				-1, // menu order
				'P');

		setApp(app);
		this.view = view;
		view.getCompanion().setDockPanel(this);

		setEmbeddedSize(300);

	}

	@Override
	public boolean canCustomizeToolbar() {
		return false;
	}

	/**
	 * 
	 * @return view
	 */
	public EuclidianViewForPlaneD getView() {
		return view;
	}

	@Override
	protected String getPlainTitle() {
		return app.getLocalization().getPlain(getViewTitle(),
				view.getTranslatedFromPlaneString());
	}

	@Override
	protected JComponent loadStyleBar() {
		return (JComponent) view.getStyleBar();
	}

	@Override
	public EuclidianView getEuclidianView() {
		return view;
	}

	@Override
	public boolean updateResizeWeight() {
		return true;
	}

	@Override
	public DockPanelData createInfo() {
		return new DockPanelData(id, toolbarString, visible, openInFrame,
				showStyleBar, new GRectangleD(frameBounds), embeddedDef,
				embeddedSize, view.getFromPlaneString());
	}

	@Override
	public boolean hasPlane() {
		return false;
	}

}
