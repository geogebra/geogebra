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
 * See https://www.geogebra.org/license for full licensing details'
 */

package org.geogebra.common.gui.view.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.common.ownership.NonOwning;

import com.google.j2objc.annotations.Weak;

/**
 * The container that manages and owns {@link AlgebraViewItem}s.
 * <p>
 * This class provides
 * <ul>
 * <li>A list of {@link AlgebraViewItem}s, one for each AV entry, holding individual AV item state
 * (e.g., input row visible, has toggle button, etc).</li>
 * <li>UI action handlers (i.e., methods clients call in response to native control events).</li>
 * <li>Sends out "content changed" notifications (subscribed to by e.g.
 * {@code AlgebraViewController} on iOS) to refresh the UI.</li>
 * <li>Methods for Kernel integration (create/delete/update items, called by
 * {@link AlgebraViewUIAdapter} upon notification from {@code Kernel}).</li>
 * </ul>
 * </p>
 * @apiNote The AV input cell, input context menu, and syntax suggestions are not part of this
 * container and need to be handled separately (and possibly differently) in platform UI.
 */
public final class AlgebraViewItems {

	/**
	 * The listener instance.
	 * @apiNote Unless we identify use cases where we would need multiple listeners,
	 * this can be a one-to-one correspondence.
	 */
	@Weak
	@NonOwning
	public Listener listener;

	/**
	 * The list of algebra view items (to show in the UI).
	 * @implNote This is a flat list (not a parent/children or otherwise grouped structure)
	 * to match the existing AlgebraAdapter.kt / AlgebraViewModel.swift code. Any grouped
	 * representation can be obtained by transforming this list - we can add methods for that
	 * as we identify grouping use cases.
	 */
	private final List<AlgebraViewItem> items = new ArrayList<>();
	private final Map<Integer, AlgebraViewItem> itemsById = new HashMap<>();
	private final Set<Integer> modifiedItemIds = new HashSet<>();
	// TODO is AtomicInteger available with GWT? otherwise use simple int
	private final AtomicInteger ITEM_ID = new AtomicInteger(1);

	private App app;

	AlgebraViewItems(@Nonnull App app) {
		this.app = app;
	}

	/**
	 * @return The number of items in the AV.
	 */
	public int getNumberOfItems() {
		return items.size();
	}

	/**
	 * Get an AV item.
	 * @param index Index (0...nrItems-1)
	 * @return The item at the given index. Throws if index is out of the valid range.
	 */
	public @Nonnull AlgebraViewItem getItem(int index) {
		return items.get(index);
	}

	/**
	 * This list of item ids can be used in the UI to compute the insertions, deletions, and moves
	 * as compared to the last snapshot. The set of modified items (that need reloading) can be
	 * independently fetched with {@link #getModifiedItemIds()}.
	 * @return The (ordered) list of item ids currently in the AV.
	 */
	public List<Integer> getItemIds() {
		return items.stream()
				.map(AlgebraViewItem::getId)
				.collect(Collectors.toList());
	}

	/**
	 * @return The ids of items that have been modified since the last reload.
	 */
	public Set<Integer> getModifiedItemIds() {
		return modifiedItemIds;
	}

	/**
	 * Retrieve items by id.
	 * @param id An item id.
	 * @return The item for the given id, or {@code null} if the id is invalid (i.e., no item
	 * with that id exists).
	 */
	public @CheckForNull AlgebraViewItem getItemById(Integer id) {
		return itemsById.get(id);
	}

	/**
	 * Reset (clear) the set of modified item ids. This happens on any call to {@link #clear()}
	 * or {@link #forceReload()}. UI can also call this method at any time (e.g., after a full
	 * reload of the AV items list).
	 */
	public void resetModifiedItemIds() {
		modifiedItemIds.clear();
	}

	private @CheckForNull AlgebraViewItem itemForGeo(@Nonnull GeoElement geo) {
		int index = items.indexOf(new AlgebraViewItem(geo));
		if (index == -1) {
			return null;
		}
		return items.get(index);
	}

	private int lastIndexWhere(Predicate<AlgebraViewItem> predicate) {
		if (items.size() > 0) {
			for (int i = items.size() - 1; i >= 0; i--) {
				if (predicate.test(items.get(i))) {
					return i;
				}
			}
		}
		return -1; // not found
	}

	private void renumberItemsFrom(int fromIndex) {
		for (int index = fromIndex; index < items.size(); index++) {
			items.get(index).index = index;
		}
	}

	// -- AlgebraViewBase integration

	/**
	 * Called after a GeoElement has been added.
	 */
	Integer onGeoAdded(@Nonnull GeoElement geo) {
		Integer itemId = ITEM_ID.getAndIncrement();
		AlgebraViewItem newItem = new AlgebraViewItem(geo, itemId);
		int lastSiblingIndex = -1;
		if (AlgebraItem.isCompactItem(geo)) {
			// find position of last geo in our list that has the same parent algo
			lastSiblingIndex = lastIndexWhere(item ->
					item.geo.getParentAlgorithm() == geo.getParentAlgorithm());
		}
		if (lastSiblingIndex == -1) {
			newItem.index = items.size();
			items.add(newItem);
		} else {
			// insert the new item after its last sibling
			items.add(lastSiblingIndex + 1, newItem);
			renumberItemsFrom(lastSiblingIndex + 1);
		}
		itemsById.put(newItem.getId(), newItem);
		if (listener != null) {
			listener.itemsChanged(false);
		}
		return newItem.getId();
	}

	/**
	 * Called after a GeoElement has been added.
	 */
	void onGeoRenamed(@Nonnull GeoElement geo) {
		AlgebraViewItem item = itemForGeo(geo);
		if (item == null) {
			return;
		}
		item.reset();
		modifiedItemIds.add(item.getId());
		if (listener != null) {
			listener.itemChanged(item);
		}
	}

	/**
	 * Called after a GeoElement has been updated (this may include visual style
	 * changes).
	 */
	void onGeoUpdated(@Nonnull GeoElement geo) {
		AlgebraViewItem item = itemForGeo(geo);
		if (item == null) {
			return;
		}
		item.reset();
		modifiedItemIds.add(item.getId());
		if (listener != null) {
			listener.itemChanged(item);
		}
	}

	/**
	 * Called after a GeoElement has been removed.
	 */
	void onGeoRemoved(@Nonnull GeoElement geo) {
		AlgebraViewItem item = itemForGeo(geo);
		if (item == null) {
			return;
		}
		modifiedItemIds.removeIf(changedItemId -> changedItemId.equals(item.getId()));
		items.remove(item);
		itemsById.remove(item.getId());
		renumberItemsFrom(item.index);
		if (listener != null) {
			listener.itemsChanged(false);
		}
	}

	/**
	 * Called when the view is cleared.
	 */
	void clear() {
		items.clear();
		itemsById.clear();
		modifiedItemIds.clear();
		if (listener != null) {
			listener.itemsChanged(false);
		}
	}

	/**
	 * Called to force a reload of all items in the UI.
	 */
	void forceReload() {
		modifiedItemIds.clear();
		if (listener != null) {
			listener.itemsChanged(true);
		}
	}

	// -- Client UI integration

	/**
	 * Call this from the UI when an item was selected in the AV. Also, reload the item afterwards
	 * (i.e., no itemChanged notification will be sent out here).
	 * @param item The selected item.
	 */
	public void select(AlgebraViewItem item) {
		app.getSelectionManager().clearSelectedGeos();
		app.getSelectionManager().addSelectedGeo(item.geo);
	}

	/**
	 * Call this from the UI when the marble was pressed for an item in the AV. Also, reload the
	 * item afterwards (i.e., no itemChanged notification will be sent out here).
	 * @param item The item.
	 */
	public void marblePressed(AlgebraViewItem item) {
		boolean active = item.getHeader().marbleState == AlgebraViewItem.MarbleState.ACTIVE;
		item.geo.setEuclidianVisible(!active);
		item.geo.updateVisualStyle(GProperty.VISIBLE);
		app.getKernel().notifyRepaint();
		app.storeUndoInfo();
	}

	/**
	 * Call this from the UI when an item's slider was moved in the AV. Also, reload the item
	 * afterwards (i.e., no itemChanged notification will be sent out here).
	 * @param item The item.
	 * @param newValue The slider value.
	 */
	public void sliderValueChanged(AlgebraViewItem item, double newValue) {
		if (!(item.geo instanceof GeoNumeric)) {
			return;
		}
		GeoNumeric numeric = (GeoNumeric) item.geo;
		numeric.setValue(newValue);
		numeric.updateCascade();
		app.getKernel().notifyRepaint();
	}

	/**
	 * Call this from the UI when the output format toggle button was pressed for an item in the
	 * AV. Also, reload the item afterwards (i.e., no itemChanged notification will be sent out
	 * here).
	 * @param item The item.
	 */
	public void outputFormatToggleButtonPressed(AlgebraViewItem item) {
		AlgebraSettings algebraSettings = app.getSettings().getAlgebra();
		boolean isEngineeringNotationEnabled = algebraSettings.isEngineeringNotationEnabled();
		Set<AlgebraOutputFormatFilter> algebraOutputFormatFilters =
				algebraSettings.getAlgebraOutputFormatFilters();
		AlgebraOutputFormat.switchToNextFormat(item.geo,
				isEngineeringNotationEnabled, algebraOutputFormatFilters);
		app.getKernel().storeUndoInfo();
		item.updateOutputFormat();
	}

	/**
	 * Call this from the UI when the play button for an item was pressed in the AV. Also, reload
	 * the item afterwards (i.e., no itemChanged notification will be sent out here).
	 * @param item The item.
	 */
	public void playButtonPressed(AlgebraViewItem item) {
		boolean animating = !(item.geo.isAnimating()
				&& item.geo.getKernel().getAnimationManager().isRunning());
		item.geo.setAnimating(animating);
		item.geo.updateRepaint();
		app.getKernel().notifyRepaint();
		if (item.geo.isAnimating()) {
			app.getKernel().getAnimationManager().startAnimation();
		}
	}

	// Nested types

	/**
	 * Algebra view items change listener.
	 * @apiNote On iOS, this will be {@code AlgebraViewController}. On Android, this will be
	 * {@code AlgebraFragment}.
	 */
	public interface Listener {
		/**
		 * Notify (the UI) that the list of items has changed.
		 * @param forceReload if {@code true}, reload all items in the UI; if {@code false},
		 * use diffing from the previous state to the current state to find out which cells need
		 * updating.
		 */
		void itemsChanged(boolean forceReload);

		/**
		 * Notify (the UI) that a specific item has changed.
		 */
		void itemChanged(AlgebraViewItem item);
	}
}
