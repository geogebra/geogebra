package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateStyleActionStore {
	private final UndoManager undoManager;
	private final List<String> initialStyleXML = new ArrayList<>();
	private final List<GeoElement> geos;

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateStyleActionStore(List<GeoElement> geosAsList) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialStyleXML.add(geo.getStyleXML());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] currentStyleXML = getCurrentStyleXML();
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);

		undoManager.buildAction(ActionType.UPDATE, currentStyleXML)
				.withUndo(ActionType.UPDATE, initialStyleXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	/**
	 * @return True if at least one element's style has changed, false else
	 */
	public boolean needUndo() {
		String[] currentStyleXML = getCurrentStyleXML();
		for (int i = 0; i < geos.size(); i++) {
			if (!currentStyleXML[i].equals(initialStyleXML.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Array consisting of the current style of the stored GeoElements
	 */
	private String[] getCurrentStyleXML() {
		return geos.stream().map(GeoElement::getStyleXML).toArray(String[]::new);
	}
}
