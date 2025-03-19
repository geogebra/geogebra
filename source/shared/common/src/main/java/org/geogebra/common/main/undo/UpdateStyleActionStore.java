package org.geogebra.common.main.undo;

import static org.geogebra.common.main.undo.ConstructionActionExecutor.RENAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.plugin.ActionType;

public class UpdateStyleActionStore {
	private final UndoManager undoManager;
	private final List<String> initialStyleXML = new ArrayList<>();
	private final List<String> initialLabels;
	private final List<GeoElement> geos;

	/**
	 * @param geosAsList selected geos
	 * @param undoManager undo manager
	 */
	public UpdateStyleActionStore(List<GeoElement> geosAsList, @Nonnull UndoManager undoManager) {
		this.geos = geosAsList;
		for (GeoElement geo: geosAsList) {
			initialStyleXML.add(geo.getStyleXML());
		}
		this.undoManager = undoManager;
		initialLabels = geos.stream().map(GeoElement::getLabelSimple).collect(Collectors.toList());
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] labels = geos.stream().map(GeoElement::getLabelSimple).toArray(String[]::new);
		List<String> actions = new ArrayList<>(geos.size());
		List<String> undoActions = new ArrayList<>(geos.size());
		for (int i = 0; i < labels.length; i++) {
			String currentStyleXML = geos.get(i).getStyleXML();
			if (!Objects.equals(labels[i], initialLabels.get(i))) {
				actions.add(RENAME + initialLabels.get(i) + " " + labels[i]);
				undoActions.add(RENAME + labels[i] + " " + initialLabels.get(i));
			}
			actions.add(currentStyleXML);
			undoActions.add(initialStyleXML.get(i));
		}
		undoManager.buildAction(ActionType.UPDATE, actions.toArray(new String[0]))
				.withUndo(ActionType.UPDATE, undoActions.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	/**
	 * @return True if at least one element's style has changed, false else
	 */
	public boolean needUndo() {
		for (int i = 0; i < geos.size(); i++) {
			if (!geos.get(i).getStyleXML().equals(initialStyleXML.get(i))) {
				return true;
			}
		}
		return false;
	}
}
