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

import static org.geogebra.common.properties.PropertyView.*;

import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.properties.PropertyView;
import org.geogebra.common.properties.impl.graphics.ProjectionsProperty;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.properties.ui.panel.IconButtonPanel;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

public class ContextMenuGraphicsWindow3DW extends ContextMenuGraphicsWindowW {

	/**
	 * @param app application
	 */
	public ContextMenuGraphicsWindow3DW(final AppW app) {
		super(app);
		buildGUI();
	}

	private void buildGUI() {
		addAxesMenuItem();
		addPlaneMenuItem();
		addGridMenuItem();
		addProjectionMenuItem();
		addSettingsButton(OptionType.EUCLIDIAN3D);
	}

	private void addPlaneMenuItem() {
		final GCheckmarkMenuItem showPlane = new GCheckmarkMenuItem(loc.getMenu("ShowPlane"),
				((Kernel3D) app.getKernel()).getXOYPlane().isPlateVisible(),
				((GuiManager3DW) app.getGuiManager()).getShowPlane3DAction()
		);
		wrappedPopup.addItem(showPlane);
	}

	private void addGridMenuItem() {
		final GCheckmarkMenuItem showGrid = new GCheckmarkMenuItem(loc.getMenu("ShowGrid"),
				((Kernel3D) app.getKernel()).getXOYPlane().isGridVisible(),
				((GuiManager3DW) app.getGuiManager()).getShowGrid3DAction()
		);
		wrappedPopup.addItem(showGrid);
	}

	private void addProjectionMenuItem() {
		SingleSelectionIconRow projectionProperty =
				(SingleSelectionIconRow) PropertyView.of(new ProjectionsProperty(loc,
						app.getEuclidianView3D(), app.getEuclidianView3D().getSettings()));

		if (projectionProperty == null) {
			return;
		}

		IconButtonPanel iconButtonPanel = new IconButtonPanel((AppW) app, projectionProperty, true,
				wrappedPopup::hide);
		AriaMenuItem projectionItem = new AriaMenuItem(iconButtonPanel, () -> {});
		projectionItem.addStyleName("iconButtonPanel projection");
		wrappedPopup.addItem(projectionItem);
	}
}
