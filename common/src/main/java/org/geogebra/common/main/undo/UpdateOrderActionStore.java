package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateOrderActionStore {
	private final UndoManager undoManager;
	private final List<Pair<String, Double>> initialOrderingValues = new ArrayList<>();

	private final List<Pair<String, Double>> updatedOrderingValues = new ArrayList<>();
	private final List<GeoElement> geos = new ArrayList<>();

	/**
	 * @param geosAsList selected geos
	 */
	public UpdateOrderActionStore(List<GeoElement> geosAsList) {
		this.geos.addAll(geosAsList);
		for (GeoElement geo: geosAsList) {
			initialOrderingValues.add(Pair.create(geo.getLabelSimple(), geo.getOrdering()));
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	private static String pairsToString(Pair<String, Double> labelOrderPair) {
		return labelOrderPair.getKey() + "," + labelOrderPair.getValue();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		for (GeoElement geo: geos) {
			updatedOrderingValues.add(Pair.create(geo.getLabelSimple(), geo.getOrdering()));
		}
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		undoManager.buildAction(ActionType.UPDATE_ORDERING, pairsToArray(updatedOrderingValues))
				.withUndo(ActionType.UPDATE_ORDERING, pairsToArray(initialOrderingValues))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	private String[] pairsToArray(List<Pair<String, Double>> labelsToOrdering) {
		return labelsToOrdering.stream().map(UpdateOrderActionStore::pairsToString)
				.toArray(String[]::new);
	}
}