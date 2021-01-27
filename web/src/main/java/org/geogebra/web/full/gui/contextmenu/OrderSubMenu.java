package org.geogebra.web.full.gui.contextmenu;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.LayerManager;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.web.full.gui.ContextMenuFactory;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.util.JsConsumer;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class OrderSubMenu extends AriaMenuBar {

	private App app;
	private LayerManager layerManager;
	private ContextMenuFactory factory;

	private ArrayList<GeoElement> geos;

	/**
	 * A submenu containing items for changing the drawing order of
	 * GeoElements on the graphics view
	 * @param geos GeoElements to apply the order-changing operations on
	 */
	public OrderSubMenu(App app, ArrayList<GeoElement> geos, ContextMenuFactory factory) {
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
		addItem(factory.newAriaMenuItem(app.getLocalization().getMenu(key),
				false, wrap(command)));
	}

	private ScheduledCommand wrap(final JsConsumer<List<GeoElement>> command) {
		return () -> {
			EuclidianView ev = app.getActiveEuclidianView();
			ev.getEuclidianController().splitSelectedStrokes(true);
			ev.getEuclidianController().widgetsToBackground();
			command.accept(geos);
			ev.invalidateDrawableList();
			app.dispatchEvent(new Event(EventType.ORDERING_CHANGE, null,
					layerManager.getOrder()));
			app.getKernel().storeUndoInfo();
		};
	}
}
