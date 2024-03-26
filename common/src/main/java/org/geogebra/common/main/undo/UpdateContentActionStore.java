package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.plugin.ActionType;

public class UpdateContentActionStore {

	private final UndoManager undoManager;
	private final List<String> initialLabelsHeightsAndContent = new ArrayList<>();
	private final ArrayList<GeoInline> geos;

	/**
	 * @param geosAsList Selected geos (GeoInline)
	 */
	public UpdateContentActionStore(ArrayList<GeoInline> geosAsList) {
		this.geos = geosAsList;
		for (GeoInline geo : geosAsList) {
			initialLabelsHeightsAndContent.add(geo.getLabelSimple());
			initialLabelsHeightsAndContent.add(Double.toString(geo.getHeight()));
			initialLabelsHeightsAndContent.add(Double.toString(geo.getContentHeight()));
			initialLabelsHeightsAndContent.add(geo.getContent());
		}
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] labels = geos.stream().map(GeoInline::getLabelSimple).toArray(String[]::new);
		String[] heights = geos.stream().map(GeoInline::getHeight)
				.map(height -> Double.toString(height)).toArray(String[]::new);
		String[] contentHeights = geos.stream().map(GeoInline::getContentHeight)
				.map(contentHeight -> Double.toString(contentHeight)).toArray(String[]::new);
		String[] currentContentXML = getCurrentContentXML();

		String[] currentLabelsHeightsAndContent = new String[currentContentXML.length * 4];
		for (int i = 0; i < currentLabelsHeightsAndContent.length; i++) {
			if (i % 4 == 0) {
				currentLabelsHeightsAndContent[i] = labels[i / 4];
			} else if (i % 4 == 1) {
				currentLabelsHeightsAndContent[i] = heights[i / 4];
			} else if (i % 4 == 2) {
				currentLabelsHeightsAndContent[i] = contentHeights[i / 4];
			} else {
				currentLabelsHeightsAndContent[i] = currentContentXML[i / 4];
			}
		}

		undoManager.buildAction(ActionType.SET_CONTENT, currentLabelsHeightsAndContent)
				.withUndo(ActionType.SET_CONTENT,
						initialLabelsHeightsAndContent.toArray(new String[0])).withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	/**
	 * @return True if at least one element's content has changed, false else
	 */
	public boolean needUndo() {
		String[] currentContentXML = getCurrentContentXML();
		for (int i = 0; i < geos.size(); i++) {
			if (!currentContentXML[i].equals(initialLabelsHeightsAndContent.get(4 * i + 3))) {
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