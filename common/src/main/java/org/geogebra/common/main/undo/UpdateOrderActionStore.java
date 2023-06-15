package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateOrderActionStore {
	private final UndoManager undoManager;
	private final List<String> initialXML = new ArrayList<>();

	private final List<String> updatedXML = new ArrayList<>();
	private final ArrayList<GeoElement> geos;
	public static final String DEL = "DEL -";

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateOrderActionStore(ArrayList<GeoElement> geosAsList) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialXML.add(geo.getLabelSimple() + DEL + geo.getOrdering());

		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/***
	 * stores updated ordering values
	 * @param geosAsList geos
	 */
	public void updateOrder(ArrayList<GeoElement> geosAsList) {
		for (GeoElement geo: geosAsList) {
			updatedXML.add(geo.getLabelSimple() + DEL + geo.getOrdering());
		}
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE_ORDERING, updatedXML.toArray(new String[0]))
				.withUndo(ActionType.UPDATE_ORDERING, initialXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}
}