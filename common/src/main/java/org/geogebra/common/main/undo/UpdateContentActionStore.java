package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.plugin.ActionType;

public class UpdateContentActionStore {

	private final UndoManager undoManager;
	private final List<String> initialContentXML = new ArrayList();
	private final ArrayList<GeoInline> geos;

	/**
	 * @param geosAsList Selected geos (GeoInline)
	 */
	public UpdateContentActionStore(ArrayList<GeoInline> geosAsList) {
		this.geos = geosAsList;
		for (GeoInline geo : geosAsList) {
			initialContentXML.add(geo.getLabelSimple());
			initialContentXML.add(geo.getContent());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] currentContentXML = getCurrentContentXML();
		String[] labels = geos.stream().map(GeoInline::getLabelSimple).toArray(String[]::new);
		String[] labelsAndContent = new String[currentContentXML.length * 2];

		for (int i = 0; i < labelsAndContent.length; i++) {
			geos.get(i / 2).notifyUpdate();
			if (i % 2 == 0) {
				labelsAndContent[i] = labels[i / 2];
			} else {
				labelsAndContent[i] = currentContentXML[i / 2];
			}
		}

		undoManager.buildAction(ActionType.SET_CONTENT, labelsAndContent)
				.withUndo(ActionType.SET_CONTENT, initialContentXML.toArray(new String[0]))
				.withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	/**
	 * @return True if at least one element's content has changed, false else
	 */
	public boolean needUndo() {
		String[] currentContentXML = getCurrentContentXML();
		for (int i = 0; i < geos.size(); i++) {
			if (!currentContentXML[i].equals(initialContentXML.get(2 * i + 1))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return Array consisting of the current content of the stored GeoInlines
	 */
	private String[] getCurrentContentXML() {
		return geos.stream().map(GeoInline::getContent).toArray(String[]::new);
	}
}