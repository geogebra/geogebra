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

package org.geogebra.common.gui.view.algebra;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.ownership.NonOwning;

import com.google.j2objc.annotations.Weak;

/**
 * An adapter between the Kernel and the Algebra View UI.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Implements the AlgebraView interface internally, and registers with the Kernel</li>
 * <li>Keeps track of AV items, handling adding/removing/updating items as requested by the
 * Kernel</li>
 * <li>Provides the list of AV items for the UI</li>
 * <li>Provides UI action handlers</li>
 * </ul>
 * <p>
 * Previously, this functionality was duplicated across AlgebraViewI/A/W/D, using subclassing
 * to handle platform-specific functionality. Now, this uses composition: a client
 * <ul>
 * <li>creates an instance of this class in the platform Algebra View UI (e.g.,
 * {@code AlgebraViewController} on iOS)</li>
 * <li>become its uiDelegate to be notified about certain UI actions (e.g., when the AV
 * input cell needs to be cleared)</li>
 * <li>and becomes its {@link AlgebraViewItems.Listener} to be notified about AV item
 * changes (e.g., to update or reload the list of items).</li>
 * </ul>
 */
public class AlgebraViewUIAdapter {

	private final AlgebraViewImpl algebraViewImpl;

	/**
	 * Constructor
	 * @param app The app.
	 */
	public AlgebraViewUIAdapter(@Nonnull App app) {
		algebraViewImpl = new AlgebraViewImpl(app);
	}

	/**
	 * Set the UI delegate and items listener.
	 * @param uiDelegate the UI delegate
	 * @param itemsListener the AV items listener
	 */
	public void setDelegates(@CheckForNull AlgebraViewUIDelegate uiDelegate,
			@CheckForNull AlgebraViewItems.Listener itemsListener) {
		algebraViewImpl.uiDelegate = uiDelegate;
		algebraViewImpl.items.listener = itemsListener;
	}

	/**
	 * @return The container that manages AV items.
	 */
	public final AlgebraViewItems getItems() {
		return algebraViewImpl.items;
	}

	/**
	 * Call from the UI when the AV visibility changes.
	 * @param isVisible whether the AV UI is visible
	 */
	public final void setVisible(boolean isVisible) {
		algebraViewImpl.setVisible(isVisible);
	}

	// -- Nested types

	@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
	private final static class AlgebraViewImpl implements AlgebraView {

		@Weak
		@NonOwning
		private @CheckForNull AlgebraViewUIDelegate uiDelegate;

		private final AlgebraViewItems items;
		private final App app;
		private final Kernel kernel;
		private final AlgebraViewVisibilityDelegate visibilityDelegate =
				new AlgebraViewVisibilityDelegate();
		private AlgebraStyle algebraStyle;
		private AlgebraView.SortMode sortMode = AlgebraView.SortMode.ORDER;
		private boolean isVisible = false;
		private boolean isWrapped = false;

		AlgebraViewImpl(@Nonnull App app) {
			this.app = app;
			this.kernel = app.getKernel();
			items = new AlgebraViewItems(app);
			kernel.attach(this);
			algebraStyle = app.getAlgebraStyle();
			addEuclidianSettingsListener();
		}

		private void addEuclidianSettingsListener() {
			if (app.isUnbundledGeometry()) { // TODO really only for standalone Geometry?? (IGR-673)
				EuclidianSettings settings = app.getSettings().getEuclidian(1);
				settings.addListener(new AxisChangeListener(this, kernel, settings));
			}
		}

		void setVisible(boolean isVisible) {
			this.isVisible = isVisible;
			visibilityDelegate.setViewVisible(isVisible);
		}

		private boolean isShown(GeoElement geo) {
			return AlgebraController.show(app, geo);
		}

		private boolean isWrappedOrShown(GeoElement geo) {
			return isWrapped || isShown(geo);
		}

		// -- org.geogebra.common.gui.view.algebra.AlgebraView --

		@Override
		public boolean isVisible() {
			return isVisible;
		}

		@Override
		public void startEditItem(GeoElement geo) {
		}

		@Override
		public boolean isEditItem() {
			return false;
		}

		@Override
		public GeoElement getDraggedGeo() {
			return null;
		}

		@Override
		public void setFocus(boolean b) {
		}

		@Override
		public GeoElement getLastSelectedGeo() {
			return null;
		}

		@Override
		public void setLastSelectedGeo(GeoElement geo) {
		}

		@Override
		public boolean isAttachedToKernel() {
			return true;
		}

		@Override
		public SortMode getTreeMode() {
			return sortMode;
		}

		@Override
		public void setTreeMode(SortMode sortMode) {
			this.sortMode = sortMode;
		}

		@Override
		public void setShowAlgebraInput(boolean visible) {
		}

		@Override
		public void doRemove(GeoElement geo) {
			items.onGeoRemoved(geo);
		}

		// -- org.geogebra.common.kernel.View --

		/**
		 * The Kernel calls this after a GeoElement has been added.
		 */
		@Override
		public void add(GeoElement geo) {
			if (isWrappedOrShown(geo)) {
				AlgebraItem.initForAlgebraView(geo);
				if (visibilityDelegate.shouldViewAdd(geo)) {
					items.onGeoAdded(geo);
				}
			}
		}

		/**
		 * The Kernel calls this after a GeoElement has been removed.
		 */
		@Override
		public void remove(GeoElement geo) {
			if (isWrappedOrShown(geo)) {
				if (visibilityDelegate.shouldViewRemove(geo)) {
					items.onGeoRemoved(geo);
				}
			}
		}

		/**
		 * The Kernel calls this after a GeoElement has been renamed.
		 */
		@Override
		public void rename(GeoElement geo) {
			if (isWrappedOrShown(geo)) {
				if (visibilityDelegate.shouldViewUpdate()) {
					items.onGeoRenamed(geo);
				}
			}
		}

		/**
		 * The Kernel calls this after a GeoElement has been updated.
		 */
		@Override
		public void update(GeoElement geo) {
			if (isWrappedOrShown(geo)) {
				if (visibilityDelegate.shouldViewUpdate()) {
					items.onGeoUpdated(geo);
				}
			}
		}

		/**
		 * The Kernel calls this after the visual style (related to property) of a GeoElement
		 * has been updated.
		 */
		@Override
		public void updateVisualStyle(GeoElement geo, GProperty property) {
			if (isWrapped || (AlgebraController.needsUpdateVisualstyle(property) && isShown(geo))) {
				if (visibilityDelegate.shouldViewUpdate()) {
					items.onGeoUpdated(geo);
				}
			}
		}

		@Override
		public void updateAuxiliaryObject(GeoElement geo) {
		}

		@Override
		public void repaintView() {
			if (visibilityDelegate.shouldViewUpdate()) {
				if (algebraStyle != app.getAlgebraStyle()
						|| visibilityDelegate.wantsViewToRepaint()) {
					this.algebraStyle = app.getAlgebraStyle();
					items.forceReload();
				}
			}
		}

		@Override
		public boolean suggestRepaint() {
			return false;
		}

		@Override
		public void reset() {
			if (uiDelegate != null) {
				uiDelegate.clearInputField();
			}
		}

		@Override
		public void clearView() {
			if (visibilityDelegate.shouldViewClear()) {
				items.clear();
			}
		}

		@Override
		public void setMode(int mode, ModeSetter m) {
		}

		@Override
		public int getViewID() {
			return App.VIEW_ALGEBRA;
		}

		@Override
		public boolean hasFocus() {
			return false;
		}

		@Override
		public void updatePreviewFromInputBar(GeoElement[] geos) {
		}

		// -- org.geogebra.common.gui.Editing --

		@Override
		public void cancelEditItem() {
		}

		@Override
		public void resetItems(boolean unselectAll) {
		}

		@Override
		public boolean isShowing() {
			return isVisible;
		}

		// -- org.geogebra.common.gui.SetLabels --

		@Override
		public void setLabels() {
		}
	}
}
