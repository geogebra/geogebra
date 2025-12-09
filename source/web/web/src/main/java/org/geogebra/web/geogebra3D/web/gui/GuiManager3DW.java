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

package org.geogebra.web.geogebra3D.web.gui;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.gui.ContextMenuGeoElementW;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.properties.PropertiesViewW;
import org.geogebra.web.full.main.GDevice;
import org.geogebra.web.geogebra3D.web.euclidian3D.EuclidianView3DW;
import org.geogebra.web.geogebra3D.web.gui.layout.panels.EuclidianDockPanel3DW;
import org.geogebra.web.geogebra3D.web.gui.view.properties.PropertiesView3DW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.Command;

/**
 * web gui manager for 3D
 * 
 * @author mathieu
 *
 */
public class GuiManager3DW extends GuiManagerW {

	private DockPanelW euclidian3Dpanel;

	/**
	 * constructor
	 * 
	 * @param app
	 *            application
	 * @param device
	 *            device (browser / tablet)
	 */
	public GuiManager3DW(AppW app, GDevice device) {
		super(app, device);
	}

	@Override
	protected boolean initLayoutPanels() {

		if (super.initLayoutPanels()) {
			this.euclidian3Dpanel = new EuclidianDockPanel3DW(getApp());
			layout.registerPanel(this.euclidian3Dpanel);
			return true;
		}

		return false;

	}

	@Override
	public DockPanelW getEuclidian3DPanel() {
		return this.euclidian3Dpanel;
	}

	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view, GPoint p) {
		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		getDrawingPadPopupMenu3D().showScaled(
				((EuclidianView3DW) view).getG2P().getElement(), p.x, p.y);
	}

	private ContextMenuGeoElementW getDrawingPadPopupMenu3D() {
		currentPopup = new ContextMenuGraphicsWindow3DW(getApp());
		return (ContextMenuGeoElementW) currentPopup;
	}

	/**
	 * 
	 * @return command to show/hide 3D axis
	 */
	public Command getShowAxes3DAction() {
		return () -> {
			// toggle axes
			((EuclidianView3DW) getApp().getEuclidianView3D()).toggleAxis();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D grid
	 */
	public Command getShowGrid3DAction() {
		return () -> {
			// toggle grid
			((EuclidianView3DW) getApp().getEuclidianView3D()).toggleGrid();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	/**
	 * 
	 * @return command to show/hide 3D plane
	 */
	public Command getShowPlane3DAction() {
		return () -> {
			// toggle plane
			((EuclidianView3DW) getApp().getEuclidianView3D())
					.getSettings().togglePlane();
			// getApp().getEuclidianView().repaint();
			getApp().storeUndoInfo();
			getApp().updateMenubar();
		};
	}

	@Override
	protected PropertiesViewW newPropertiesViewW(AppW app1, OptionType optionType) {
		return new PropertiesView3DW(app1, optionType);
	}

}
