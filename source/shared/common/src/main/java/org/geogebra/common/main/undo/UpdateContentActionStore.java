/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main.undo;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.plugin.ActionType;

public class UpdateContentActionStore {

	private final UndoManager undoManager;
	private final List<String> initialLabelsHeightsAndContent;
	private final List<GeoInline> geos;

	/**
	 * @param geosAsList Selected geos (GeoInline)
	 */
	public UpdateContentActionStore(List<GeoInline> geosAsList) {
		this.geos = geosAsList;
		initialLabelsHeightsAndContent = buildContentAndHeightList();
		this.undoManager = geosAsList.get(0).getConstruction().getUndoManager();
	}

	private List<String> buildContentAndHeightList() {
		ArrayList<String> contentList = new ArrayList<>(4 * geos.size());
		for (GeoInline geo : geos) {
			contentList.add(geo.getLabelSimple());
			contentList.add(Double.toString(geo.getHeight()));
			contentList.add(Double.toString(geo.getContentHeight()));
			contentList.add(geo.getContent());
		}
		return contentList;
	}

	/**
	 * Store undoable action
	 */
	public void storeUndo() {
		String[] labels = geos.stream().map(GeoInline::getLabelSimple).toArray(String[]::new);
		String[] currentLabelsHeightsAndContent = buildContentAndHeightList()
				.toArray(new String[0]);
		undoManager.buildAction(ActionType.SET_CONTENT, currentLabelsHeightsAndContent)
				.withUndo(ActionType.SET_CONTENT,
						initialLabelsHeightsAndContent.toArray(new String[0])).withLabels(labels)
				.storeAndNotifyUnsaved();
	}

	/**
	 * @return True if at least one element's content has changed, false else
	 */
	public boolean needUndo() {
		for (int i = 0; i < geos.size(); i++) {
			if (!geos.get(i).getContent().equals(initialLabelsHeightsAndContent.get(4 * i + 3))) {
				return true;
			}
		}
		return false;
	}
}