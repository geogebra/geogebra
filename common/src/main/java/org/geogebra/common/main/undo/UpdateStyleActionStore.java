package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateStyleActionStore {
	private final UndoManager undoManager;
	private final List<String> initialXML = new ArrayList<>();
	private final ArrayList<GeoElement> geos;

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateStyleActionStore(ArrayList<GeoElement> geosAsList) {
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
		undoManager.storeUndoableAction(ActionType.UPDATE,
				geos.stream().map(GeoElement::getStyleXML).toArray(String[]::new),
				ActionType.UPDATE, initialXML.toArray(new String[0]));
	}
}