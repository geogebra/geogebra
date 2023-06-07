package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateOrderActionStore {
	private final UndoManager undoManager;
	private final List<String> initialXML = new ArrayList<>();

	private final List<String> updatedXML = new ArrayList<>();
	private final List<GeoElement> geos = new ArrayList<>();

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateOrderActionStore(List<GeoElement> geosAsList) {
		this.geos.addAll(geosAsList);
		for (GeoElement geo: geosAsList) {
			initialXML.add(geo.getStyleXML());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		for (GeoElement geo: geos) {
			updatedXML.add(geo.getStyleXML());
		}
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE, updatedXML.toArray(new String[0]))
				.withUndo(ActionType.UPDATE, initialXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}
}