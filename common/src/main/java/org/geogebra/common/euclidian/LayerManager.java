package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusStroke;
import org.geogebra.common.kernel.geos.groups.Group;
import org.geogebra.common.main.undo.UpdateOrderActionStore;
import org.geogebra.common.util.CopyPaste;

public class LayerManager {

	private ArrayList<GeoElement> drawingOrder = new ArrayList<>();
	private boolean renaming = false;

	private int getNextOrder() {
		return drawingOrder.size();
	}

	/**
	 * Add geo on the last position and set its ordering
	 */
	public void addGeo(GeoElement geo) {
		if (renaming) {
			return;
		}
		if (geo instanceof GeoLocusStroke) {
			GeoLocusStroke stroke = (GeoLocusStroke) geo;
			if (stroke.getSplitParentLabel() != null) {
				drawingOrder.add(insertInCorrectPosition(stroke), geo);
				return;
			}
		}

		if (!geo.isMask() && !geo.isMeasurementTool()) {
			geo.setOrdering(getNextOrder());
			geo.setDepth(getNextOrder()); //sets depth to the size of the list
			//might not work...will somehow have to get the depth of the first element in front
			//and add to that 1 or something
			drawingOrder.add(geo);
		}
	}

	/**
	 * Remove the geo and update the ordering of all other elements
	 */
	public void removeGeo(GeoElement geo) {
		if (renaming) {
			return;
		}
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
		UpdateOrderActionStore store = new UpdateOrderActionStore(drawingOrder); //todo: drawingorder oder selection?
		if (isGroupMember(selection)) {
			moveGroupMemberForward(selection.get(0));
		} else {
			moveSelectionForward(selection);
		}
		updateOrdering();
		store.updateDepth(drawingOrder);
		store.storeUndo();
	}

	private void moveSelectionForward(List<GeoElement> selection) {
		ArrayList<GeoElement> result = new ArrayList<>(drawingOrder.size());

		int idx = addGeosBefore(selection, result);
		idx = forwardByGeos(result, idx);
		addSelectionSorted(result, selection);
		addRemainingToForwardOrder(result, idx);

		drawingOrder = result;
	}

	private int addGeosBefore(List<GeoElement> selection, ArrayList<GeoElement> order) {
		int i = 0;
		int skipped = 0;
		while (i < drawingOrder.size() && skipped < selection.size()) {
			GeoElement geo = drawingOrder.get(i);
			if (selection.contains(geo)) {
				skipped++;
			} else {
				order.add(geo);
			}
			i++;
		}
		return i;
	}

	private void addRemainingToForwardOrder(ArrayList<GeoElement> resultingOrder, int index) {
		int idx = index;
		while (idx < drawingOrder.size()) {
			resultingOrder.add(drawingOrder.get(idx));
			idx++;
		}
	}

	private int forwardByGeos(ArrayList<GeoElement> resultingOrder, int index) {
		int idx = index;
		if (idx < drawingOrder.size()) {
			resultingOrder.add(drawingOrder.get(idx));
			idx++;
		}

		// Add all elements in the same group as the geo in front
		while (idx < drawingOrder.size() && drawingOrder.get(idx).hasGroup()
				&& getGroupOf(idx - 1) == getGroupOf(idx)) {
			resultingOrder.add(drawingOrder.get(idx));
			idx++;
		}
		return idx;
	}

	/**
	 * Move the selection exactly one step behind the one with the
	 * lowest priority in the selection
	 */
	public void moveBackward(List<GeoElement> selection) {
		UpdateOrderActionStore store = new UpdateOrderActionStore(drawingOrder);
		if (isGroupMember(selection)) {
			moveGroupMemberBackward(selection.get(0));
		} else {
			moveSelectionBackward(selection);
		}

		updateOrdering();
		store.updateDepth(drawingOrder);
		store.storeUndo();
	}

	private void moveSelectionBackward(List<GeoElement> selection) {
		ArrayList<GeoElement> result = new ArrayList<>(drawingOrder.size());
		int idx = insertGeosBefore(selection, result);
		idx = insertGroupedGeos(result, idx);
		insertSelectionSorted(result, selection);
		insertRemainingGeos(result, selection, idx);
		drawingOrder = result;
	}

	private void insertRemainingGeos(ArrayList<GeoElement> result,
									 List<GeoElement> selection, int index) {
		int idx = index;
		while (idx >= 0) {
			if (!selection.contains(drawingOrder.get(idx))) {
				result.add(0, drawingOrder.get(idx));
			}
			idx--;
		}
	}

	private int insertGroupedGeos(ArrayList<GeoElement> result, int index) {
		int idx = index;
		if (idx >= 0) {
			result.add(0, drawingOrder.get(idx));
			idx--;
		}
		// Add all elements in the same group as the geo behind
		while (idx >= 0 && drawingOrder.get(idx).hasGroup()
				&& getGroupOf(idx + 1) == getGroupOf(idx)) {
			result.add(0, drawingOrder.get(idx));
			idx--;
		}
		return idx;

	}

	private int insertGeosBefore(List<GeoElement> selection, ArrayList<GeoElement> order) {
		int i = drawingOrder.size() - 1;
		int skipped = 0;
		while (i >= 0 && skipped < selection.size()) {
			GeoElement geo = drawingOrder.get(i);
			if (selection.contains(geo)) {
				skipped++;
			} else {
				order.add(0, geo);
			}
			i--;
		}

		return i;
	}

	private void insertSelectionSorted(List<GeoElement> to, List<GeoElement> from) {
		List<GeoElement> copy = new ArrayList<>(from);
		sortByOrder(copy);
		to.addAll(0, copy);
	}

	/**
	 * Move the selected geos to the top of the drawing priority list
	 * while respecting their relative ordering
	 */
	public void moveToFront(List<GeoElement> selection) {
		UpdateOrderActionStore store = new UpdateOrderActionStore(drawingOrder);
		if (isGroupMember(selection)) {
			moveGroupMemberToFront(selection.get(0));
		} else {
			moveSelectionToFront(selection);
		}
		store.updateDepth(drawingOrder);
		store.storeUndo();
	}

	private void moveSelectionToFront(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());

		for (GeoElement geo : drawingOrder) {
			if (!selection.contains(geo)) {
				resultingOrder.add(geo);
			}
		}

		addSelectionSorted(resultingOrder, selection);
		drawingOrder = resultingOrder;
	}

	/**
	 * Move the selected geos to the botom of the drawing priority list
	 * while respecting their relative ordering
	 */
	public void moveToBack(List<GeoElement> selection) {
		UpdateOrderActionStore store = new UpdateOrderActionStore(drawingOrder);
		if (isGroupMember(selection)) {
			moveGroupMemberToBack(selection.get(0));
		} else {
			moveSelectionToBack(selection);
		}
		store.updateDepth(drawingOrder);
		store.storeUndo();
	}

	private void moveSelectionToBack(List<GeoElement> selection) {
		ArrayList<GeoElement> resultingOrder = new ArrayList<>(drawingOrder.size());
		addSelectionSorted(resultingOrder, selection);

		for (GeoElement geo : drawingOrder) {
			if (!selection.contains(geo)) {
				resultingOrder.add(geo);
			}
		}

		drawingOrder = resultingOrder;
	}

	/**
	 * Move the drawing layer of the members of the group next to each other
	 * @param groupMembers members of the group
	 */
	public void groupObjects(ArrayList<GeoElement> groupMembers) {
		ArrayList<GeoElement> result = new ArrayList<>(drawingOrder.size());

		int idx = addGeosBefore(groupMembers, result);
		addSelectionSorted(result, groupMembers);
		addRemainingToForwardOrder(result, idx);

		drawingOrder = result;
		updateOrdering();
	}

	/**
	 * @param labels comma separated list of labels
	 * @param kernel kernel for geo lookup
	 */
	public void updateOrdering(String labels, Kernel kernel) {
		drawingOrder.clear();
		int counter = 0;
		for (String label : labels.split(",")) {
			GeoElement geo = kernel.lookupLabel(label);
			drawingOrder.add(geo);
			geo.setOrdering(counter++);
		}
	}

	/**
	 * Moves geo to the top of drawables
	 * within its group.
	 *
	 * @param geo to move front.
	 */
	private void moveGroupMemberToFront(GeoElement geo) {
		moveTo(geo, lastIndexOf(geo.getParentGroup()), ObjectMovement.FRONT);
	}

	/**
	 * Moves geo to the bottom of drawables
	 * within its group.
	 *
	 * @param geo to move back.
	 */
	private void moveGroupMemberToBack(GeoElement geo) {
		moveTo(geo, firstIndexOf(geo.getParentGroup()), ObjectMovement.BACK);
	}

	private void moveTo(GeoElement geo, int index, ObjectMovement movement) {
		int srcIdx = indexOf(geo);
		if (srcIdx != index) {
			drawingOrder.remove(geo);
			drawingOrder.add(index, geo);
			updateDepth(geo, movement);
		}
	}

	/**
	 * Moves geo one step forward in the drawables
	 * within its group.
	 *
	 * @param geo to move forward.
	 */
	private void moveGroupMemberForward(GeoElement geo) {
		int index = indexOf(geo);
		if (index < lastIndexOf(geo.getParentGroup())) {
			Collections.swap(drawingOrder, index, index + 1);
			updateDepth(geo, ObjectMovement.FORWARD);
		}
	}

	/**
	 * Moves geo one step backward in the drawables
	 * within its group.
	 *
	 * @param geo to move backward.
	 */
	private void moveGroupMemberBackward(GeoElement geo) {
		int index = indexOf(geo);
		if (index > firstIndexOf(geo.getParentGroup())) {
			Collections.swap(drawingOrder, index, index - 1);
			updateDepth(geo, ObjectMovement.BACKWARD);
		}
	}

	private void updateDepth(GeoElement geo, ObjectMovement movement) {

		int index = indexOf(geo);
		int firstIndex = firstIndexOf(geo.getParentGroup());
		int lastIndex = lastIndexOf(geo.getParentGroup());

		switch (movement) {
		case BACKWARD:
		case BACK: if (index + 1 >= lastIndex && index - 1 <= firstIndex) {
			geo.setDepth(orderingDepthMidpoint(index));
			geo.setOrdering(orderingDepthMidpoint(index));
		} else {
			if (index == firstIndex) {
				if (index != 0) { //first one in group but not in ordering list
					geo.setDepth(orderingDepthMidpoint(index));
					geo.setOrdering(orderingDepthMidpoint(index));
				} //else, first in group & order thing
				else {
					geo.setDepth(drawingOrder.get(index + 1).getDepth() - 1);
					geo.setOrdering(drawingOrder.get(index + 1).getOrdering() - 1);
				}
			} else {
				if (index == lastIndex) {
					if (index != drawingOrder.size() - 1) { //last one in group but not in ordering
						geo.setDepth(orderingDepthMidpoint(index));
						geo.setOrdering(orderingDepthMidpoint(index));
					} //else, first in group & order thing
					else {
						geo.setDepth(drawingOrder.get(index - 1).getDepth() + 1);
						geo.setOrdering(drawingOrder.get(index - 1).getOrdering() + 1);
					}
				}
			}
		}
			break;
		case FRONT:
		case FORWARD:
			if (index + 1 <= lastIndex && index - 1 >= firstIndex) {
				geo.setDepth(orderingDepthMidpoint(index));
				geo.setOrdering(orderingDepthMidpoint(index));
			} else {
				if (index == lastIndex) {
					if (index != drawingOrder.size() - 1) { //last one in group but not in ordering
						geo.setDepth(orderingDepthMidpoint(index));
						geo.setOrdering(orderingDepthMidpoint(index));
					} //else, last in group & order thing
					else {
						geo.setDepth(drawingOrder.get(index - 1).getDepth() + 1);
						geo.setOrdering(drawingOrder.get(index - 1).getOrdering() + 1);
					}
				}  else {
					if (index == firstIndex) {
						if (index != 0) { //first one in group but not in ordering
							geo.setDepth(orderingDepthMidpoint(index));
							geo.setOrdering(orderingDepthMidpoint(index));
						} //else, first in group & order thing
						else {
							geo.setDepth(drawingOrder.get(index - 1).getDepth() - 1);
							geo.setOrdering(drawingOrder.get(index - 1).getOrdering() - 1);
						}
					}
				} //else depth stays the same (double check this)
			}
			break;
		}
	}

	public float orderingDepthMidpoint(int index) {
		return (drawingOrder.get(index + 1).getOrdering() + drawingOrder.get(index - 1)
				.getOrdering()) / 2;
	}

	private int indexOf(GeoElement geo) {
		return drawingOrder.indexOf(geo);
	}

	private int firstIndexOf(Group group) {
		return drawingOrder.indexOf(group.getMinByOrder());
	}

	private int lastIndexOf(Group group) {
		return drawingOrder.indexOf(group.getMaxByOrder());
	}

	private boolean isGroupMember(List<GeoElement> selection) {
		return selection.size() == 1 && selection.get(0).hasGroup();
	}

	private Group getGroupOf(int i) {
		return drawingOrder.get(i).getParentGroup();
	}

	private void updateOrdering() {
		for (int i = 0; i < drawingOrder.size(); i++) {
			drawingOrder.get(i).setOrdering(i);
		} //TODO: set the undo/redo point here?
	}

	private int insertInCorrectPosition(GeoLocusStroke stroke) {

		float order = stroke.getConstruction()
				.lookupLabel(stroke.getSplitParentLabel()).getOrdering(); //TODO: hmm.

		int insertionIndex = getInsertionIndex(stroke);

		return insertionIndex;
	}

	private int getInsertionIndex(GeoElement geo) {
		int insertionIndex = Collections.binarySearch(drawingOrder, geo, Group.orderComparator);
		if (insertionIndex < 0) {
			insertionIndex = - insertionIndex - 1; // Convert to the actual insertion point
		}
		return insertionIndex;
	}

	private void addSelectionSorted(List<GeoElement> to, List<GeoElement> from) {
		List<GeoElement> copy = new ArrayList<>(from);
		sortByOrder(copy);
		to.addAll(copy);
	}

	private void sortByOrder(List<GeoElement> copy) {
		copy.sort((a, b) -> {
			if (isPasted(a) && !isPasted(b)) {
				return 1;
			}
			if (isPasted(b) && !isPasted(a)) {
				return -1;
			}
			if (a.getOrdering() - b.getOrdering() != 0) {
				return (int)a.getOrdering() - (int)b.getOrdering(); //TODO: hi
			}
			// delete, undo => the *new* element with the same ordering should be lower
			return b.getConstructionIndex() - a.getConstructionIndex();
		});
	}

	private boolean isPasted(GeoElement a) {
		return a.getLabelSimple() != null && a.getLabelSimple().startsWith(CopyPaste.labelPrefix);
	}

	/**
	 * Update the list from geos
	 */
	public void updateList() {
		sortByOrder(drawingOrder);
		updateOrdering(); // remove potential gaps
	}

	public void setRenameRunning(boolean renaming) {
		this.renaming = renaming;
	}

	/**
	 * @return comma separated labels in drawing order
	 */
	public String getOrder() {
		StringBuilder sb = new StringBuilder();
		for (GeoElement geo: drawingOrder) {
			if (sb.length() != 0) {
				sb.append(",");
			}
			sb.append(geo.getLabelSimple());
		}
		return sb.toString();
	}

	/**
	 * @param pos new ordering
	 * @param newGeo construction element
	 */
	public void replace(int pos, GeoElement newGeo) {
		drawingOrder.remove(newGeo);
		if (drawingOrder.size() >= pos && pos >= 0) {
			drawingOrder.add(pos, newGeo);
			updateOrdering();
			newGeo.getKernel().getApplication()
					.getActiveEuclidianView().invalidateDrawableList();
		}
	}
}
