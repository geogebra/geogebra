package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateOrderActionStore {
	private final UndoManager undoManager;
	private final HashMap<String, Float> initialDepthSnapshot = new HashMap<String, Float>();

	private final HashMap<String, Float> modifiedDepthSnapshot = new HashMap<String, Float>();
	private final ArrayList<GeoElement> geos;

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateOrderActionStore(ArrayList<GeoElement> geosAsList) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialDepthSnapshot.put(geo.getLabelSimple(), geo.getDepth());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	public void updateDepth(ArrayList<GeoElement> geosAsList) {
		for (GeoElement geo: geosAsList) {
			modifiedDepthSnapshot.put(geo.getLabelSimple(), geo.getDepth());
		}
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] initialXML = geos.stream().map(
				geo -> {
					GeoElement temp = geo;
					float initialOrdering = initialDepthSnapshot.get(temp.getLabelSimple());
					temp.setDepth(initialOrdering);
					temp.setOrdering(initialOrdering);
					return temp.getStyleXML();
				}
			).toArray(String[]::new);

		String[] currentXML = geos.stream().map(GeoElement::getStyleXML).toArray(String[]::new);

		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE, currentXML)
				.withUndo(ActionType.UPDATE, initialXML)
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}
}