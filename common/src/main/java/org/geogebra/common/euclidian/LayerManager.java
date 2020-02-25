package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LayerManager {

	private List<GeoElement> drawingOrder = new ArrayList<>();

	private int getNextOrder() {
		return drawingOrder.size();
	}

	/**
	 * Add geo on the last position and set its ordering
	 */
	public void addGeo(GeoElement geo) {
		if (geo instanceof GeoLocusStroke) {
			GeoLocusStroke stroke = (GeoLocusStroke) geo;
			if (stroke.getSplitParentLabel() != null) {
				int order = stroke.getConstruction()
						.lookupLabel(stroke.getSplitParentLabel()).getOrdering();
				drawingOrder.add(order, geo);
				updateOrdering();
				return;
			}
		}

		if (!geo.isMask()) {
			geo.setOrdering(getNextOrder());
			drawingOrder.add(geo);
		}
	}

	/**
	 * Remove the geo and update the ordering of all other elements
	 */
	public void removeGeo(GeoElement geo) {
		drawingOrder.remove(geo);
		updateOrdering();
	}

	public void clear() {
		drawingOrder.clear();
	}

	/**
	 * Move the geos in the selection exactly one step in front of the
	 * one with the highest priority in the selection
	 */
	public void moveForward(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());
		int i = 0;
		int found = 0;

		while (i < drawingOrder.size() && found < selection.size()) {
			if (selection.contains(drawingOrder.get(i))) {
				found++;
			} else {
				resultingOrder.add(drawingOrder.get(i));
			}
			i++;
		}

		// Add one more element to _move forward_
		if (i < drawingOrder.size()) {
			resultingOrder.add(drawingOrder.get(i));
			i++;
		}

		addSorted(resultingOrder, selection);

		while (i < drawingOrder.size()) {
			resultingOrder.add(drawingOrder.get(i));
			i++;
		}

		drawingOrder = resultingOrder;
		updateOrdering();
	}

	/**
	 * Move the selection exactly one step behind the one with the
	 * lowest priority in the selection
	 */
	public void moveBackward(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());
		int i = 0;

		if (!selection.contains(drawingOrder.get(0))) {
			while (i < drawingOrder.size() && !selection.contains(drawingOrder.get(i + 1))) {
				resultingOrder.add(drawingOrder.get(i));
				i++;
			}
		}

		addSorted(resultingOrder, selection);

		while (i < drawingOrder.size()) {
			if (!selection.contains(drawingOrder.get(i))) {
				resultingOrder.add(drawingOrder.get(i));
			}
			i++;
		}

		drawingOrder = resultingOrder;
		updateOrdering();
	}

	/**
	 * Move the selected geos to the top of the drawing priority list
	 * while respecting their relative ordering
	 */
	public void moveToFront(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());

		for (GeoElement geo : drawingOrder) {
			if (!selection.contains(geo)) {
				resultingOrder.add(geo);
			}
		}

		addSorted(resultingOrder, selection);

		drawingOrder = resultingOrder;
		updateOrdering();
	}

	/**
	 * Move the selected geos to the botom of the drawing priority list
	 * while respecting their relative ordering
	 */
	public void moveToBack(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());

		addSorted(resultingOrder, selection);

		for (GeoElement geo : drawingOrder) {
			if (!selection.contains(geo)) {
				resultingOrder.add(geo);
			}
		}

		drawingOrder = resultingOrder;
		updateOrdering();
	}

	private void updateOrdering() {
		for (int i = 0; i < drawingOrder.size(); i++) {
			drawingOrder.get(i).setOrdering(i);
		}
	}

	private void addSorted(List<GeoElement> to, List<GeoElement> from) {
		List<GeoElement> copy = new ArrayList<>(from);
		Collections.sort(copy, new Comparator<GeoElement>() {
			@Override
			public int compare(GeoElement a, GeoElement b) {
				return a.getOrdering() - b.getOrdering();
			}
		});
		to.addAll(copy);
	}
}
