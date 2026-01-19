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

package org.geogebra.common.euclidian;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.main.undo.UndoCommandBuilder;
import org.geogebra.common.main.undo.UndoManager;
import org.geogebra.common.plugin.ActionType;

import com.google.j2objc.annotations.Weak;

/**
 * Converts changes (updates) to currently selected elements into
 * {@link org.geogebra.common.main.undo.UndoCommand UndoCommand}s (by snapshotting their definition)
 * and stores the resulting undo commands in the {@link UndoManager}.
 */
public class UpdateActionStore {
	private final List<UndoItem> undoItems = new ArrayList<>();

	@Weak
	protected final SelectionManager selection;
	private final UndoManager undoManager;
	private boolean stitching;

	/**
	 * Constructor
	 * @param selection {@link SelectionManager}
	 * @param undoManager {@link UndoManager}
	 */
	public UpdateActionStore(SelectionManager selection, UndoManager undoManager) {
		this.selection = selection;
		this.undoManager = undoManager;
	}

	/**
	 * Store selected geo to items.
	 * @param moveMode active move mode
	 */
	public void storeSelection(MoveMode moveMode) {
		if (undoItems.isEmpty()) {
			storeItems(moveMode);
		}
	}

	private void storeItems(MoveMode defaultMode) {
		for (GeoElement geo : selection.getSelectedGeos()) {
			if (geo.hasChangeableParent3D()) {
				GeoNumeric num = geo.getChangeableParent3D().getNumber();
				if (num.isLabelSet()) {
					undoItems.add(new UndoItem(num, MoveMode.NUMERIC));
				} else {
					undoItems.add(new UndoItem(geo.getChangeableParent3D().getSurface(),
							defaultMode));
				}
				continue;
			}
			if (geo.getParentAlgorithm() != null
					&& !geo.isPointOnPath() && !geo.isPointInRegion()) {
				addAll(geo.getParentAlgorithm().getDefinedAndLabeledInput(), defaultMode);
			} else if (geo instanceof GeoImage) {
				addAll(((GeoImage) geo).getDefinedAndLabeledStartPoints(), defaultMode);
			}
			undoItems.add(new UndoItem(geo, defaultMode));
		}
	}

	private void addAll(List<? extends GeoElement> geos, MoveMode mode) {
		geos.forEach(geo -> undoItems.add(new UndoItem(geo, mode)));
	}

	/**
	 * Add a single element if not already present
	 * @param geo element to add
	 * @param mode move mode
	 */
	public void addIfNotPresent(GeoElement geo, MoveMode mode) {
		if (undoItems.stream().noneMatch(it -> it.hasGeo(geo))) {
			undoItems.add(new UndoItem(geo, mode));
		}
	}

	/**
	 * Remove all items related to given element.
	 * @param geo element
	 */
	public void remove(GeoElement geo) {
		undoItems.removeIf(it -> it.hasGeo(geo));
	}

	/**
	 * Clear all items.
	 */
	public void clear() {
		undoItems.clear();
	}

	/**
	 * Builds actions from items and stores it in UndoManager
	 */
	public void storeUpdateAction() {
		List<String> actions = new ArrayList<>(undoItems.size());
		List<String> undoActions = new ArrayList<>(undoItems.size());
		List<String> labels = new ArrayList<>(undoItems.size());
		for (UndoItem item: undoItems) {
			actions.add(item.content());
			undoActions.add(item.previousContent());
			labels.add(item.getLabel());
		}
		UndoCommandBuilder builder = undoManager
				.buildAction(ActionType.UPDATE, actions.toArray(new String[0]))
				.withUndo(ActionType.UPDATE, undoActions.toArray(new String[0]))
				.withLabels(labels.toArray(new String[0]));
		if (stitching) {
			builder.withStitchToNext();
		}
		builder.storeAndNotifyUnsaved();
	}

	/**
	 * Store undo
	 * @return if there is items in undo list.
	 */
	public boolean storeUndo() {
		if (!undoItems.isEmpty()) {
			storeUpdateAction();
		}
		return undoItems.isEmpty();
	}

	/**
	 * Store undo
	 * @return if there is items in undo list.
	 */
	public boolean isEmpty() {
		return undoItems.isEmpty();
	}

	public void setStitching(boolean stitching) {
		this.stitching = stitching;
	}
}
