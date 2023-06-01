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

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateOrderActionStore(ArrayList<GeoElement> geosAsList) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialXML.add(geo.getStyleXML());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	public void updateOrder(ArrayList<GeoElement> geosAsList) {
		for (GeoElement geo: geosAsList) {
			updatedXML.add(geo.getStyleXML());
		}
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE, updatedXML.toArray(new String[0]))
				.withUndo(ActionType.UPDATE, initialXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}
}