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

package org.geogebra.web.full.gui.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.LayerManager;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.gwtutil.JsConsumer;
import org.geogebra.web.full.gui.ContextMenuItemFactory;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;

public class OrderSubMenu extends AriaMenuBar {

	private App app;
	private LayerManager layerManager;
	private ContextMenuItemFactory factory;

	private ArrayList<GeoElement> geos;

	/**
	 * A submenu containing items for changing the drawing order of
	 * GeoElements on the graphics view
	 * @param geos GeoElements to apply the order-changing operations on
	 */
	public OrderSubMenu(App app, ArrayList<GeoElement> geos, ContextMenuItemFactory factory) {
		this.geos = geos;

		this.app = app;
		this.layerManager = app.getKernel().getConstruction().getLayerManager();
		this.factory = factory;

		addItem("ContextMenu.BringToFront", layerManager::moveToFront);
		addItem("ContextMenu.BringForward", layerManager::moveForward);
		addItem("ContextMenu.SendBackward", layerManager::moveBackward);
		addItem("ContextMenu.SendToBack", layerManager::moveToBack);
	}

	private void addItem(String key, JsConsumer<List<GeoElement>> command) {
		addItem(factory.newAriaMenuItem((ResourcePrototype) null,
				app.getLocalization().getMenu(key), wrap(command)));
	}

	private ScheduledCommand wrap(final JsConsumer<List<GeoElement>> command) {
		return () -> {
			EuclidianView ev = app.getActiveEuclidianView();
			ev.getEuclidianController().splitSelectedStrokes(true);
			ev.getEuclidianController().widgetsToBackground();
			command.accept(geos);
			ev.invalidateDrawableList();
		};
	}
}
