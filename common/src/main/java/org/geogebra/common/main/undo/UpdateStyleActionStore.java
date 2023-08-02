package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateStyleActionStore {
	private final UndoManager undoManager;
	private final List<String> initialXML = new ArrayList<>();
	private final List<GeoElement> geos;

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateStyleActionStore(List<GeoElement> geosAsList) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialXML.add(geo.getStyleXML());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] currentXML = geos.stream().map(GeoElement::getStyleXML).toArray(String[]::new);
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE, currentXML)
				.withUndo(ActionType.UPDATE, initialXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}
}
