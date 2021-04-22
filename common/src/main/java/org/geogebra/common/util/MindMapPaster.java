package org.geogebra.common.util;

import java.util.ArrayList;

import org.geogebra.common.euclidian.draw.DrawMindMap;
import org.geogebra.common.kernel.geos.GeoMindMapNode;
import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

public class MindMapPaster {
	private GeoMindMapNode target;

	/**
	 * @param app application
	 */
	public MindMapPaster(App app) {
		SelectionManager selection = app.getSelectionManager();
		if (selection.selectedGeosSize() == 1 && app.isWhiteboardActive()
				&& selection.getSelectedGeos().get(0) instanceof GeoMindMapNode) {
			target = ((GeoMindMapNode) selection.getSelectedGeos().get(0));
		}
	}

	/**
	 * Update position and parent references
	 * @param mindMaps mind-map nodes
	 */
	public void update(ArrayList<GeoMindMapNode> mindMaps) {
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
