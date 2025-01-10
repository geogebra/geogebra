package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.euclidian.draw.DrawMindMap;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

public class MindMapPaster {
	private GeoMindMapNode target;

	/**
	 * If the current selection contains only a single MindMapNode save it
	 * so that we can append to it the pasted branch
	 * @param selection SelectionManager
	 */
	public void setTargetFromSelection(SelectionManager selection) {
		// allows paste
		target = (GeoMindMapNode) selection.getSelectedGeos().stream()
				.filter(geo -> geo instanceof GeoMindMapNode).findFirst().orElse(null);
	}

	/**
	 * Update position and parent references
	 * @param mindMaps mind-map nodes
	 */
	public void joinToTarget(ArrayList<GeoMindMapNode> mindMaps) {
		for (GeoMindMapNode mindMapNode: mindMaps) {
			fixPosition(mindMapNode);
		}
		for (GeoMindMapNode mindMapNode: mindMaps) {
			if (mindMapNode.isParentPending()) {
				mindMapNode.resolvePendingParent(target);
			}
			if (mindMapNode.getParent() == null) {
				mindMapNode.setAlignment(null); // subtree pasted as new map
			}
		}
	}

	private void fixPosition(GeoMindMapNode mindMapNode) {
		App app = mindMapNode.getKernel().getApplication();
		DrawMindMap dm = (DrawMindMap) app.getActiveEuclidianView().getDrawableFor(mindMapNode);
		if (dm != null) {
			if (mindMapNode.isParentPending()) {
				dm.fixPosition(target);
			} else {
				dm.fixPosition(mindMapNode.getParent());
			}
		}
	}
}
