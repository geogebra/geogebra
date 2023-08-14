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
		String[] currentXML = geos.stream().map(GeoElement::getStyleXML).toArray(String[]::new);
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);

		//Only store undoable action if XML changed
		StringBuilder currentXMLString = new StringBuilder();
		StringBuilder initialXMLString = new StringBuilder();
		for (int i = 0; i < geos.size(); i++) {
			currentXMLString.append(currentXML[i]);
			initialXMLString.append(initialXML.get(i));
		}

		if (!currentXMLString.toString().contentEquals(initialXMLString)) {
			undoManager.buildAction(ActionType.UPDATE, currentXML)
					.withUndo(ActionType.UPDATE, initialXML.toArray(new String[0]))
					.withLabels(labels)
					.storeAndNotifyUnsaved();
		}
	}
}
