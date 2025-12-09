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

package org.geogebra.common.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.main.SelectionManager;

public class GeoTabber implements MayHaveFocus {

	private boolean selected;
	private final SelectionManager selectionManager;
	private final App app;

	/**
	 * @param app Application
	 */
	public GeoTabber(App app) {
		selectionManager = app.getSelectionManager();
		this.app = app;
	}

	@Override
	public boolean focusIfVisible(boolean reverse) {
		boolean isAlgebraViewFocused = app.isAlgebraViewFocused();
		selectionManager.clearSelectedGeos(false);
		// selectNext / selectPrevious are responsible also for moving the focus -- they have
		// to decide if it goes to inputbox or canvas. We should not move focus here.
		if (reverse) {
			selected = selectionManager.selectPreviousGeo();
		} else {
			selected = selectionManager.selectNextGeo();
		}
		if (isAlgebraViewFocused) {
			setAlgebraViewAsFocusedPanel();
		}
		return selected;
	}

	@Override
	public boolean hasFocus() {
		return selected;
	}

	/**
	 * Focuses the next available GeoElement
	 * <p>
	 * If the Algebra View is currently focused, this method ensures that the focused panel,
	 * after the next GeoElement has been selected, remains the AlgebraView
	 * </p>
	 * @return True if the selection of the next geo was successful, false else
	 */
	@Override
	public boolean focusNext() {
		boolean isAlgebraViewFocused = app.isAlgebraViewFocused();
		selected = selectionManager.selectNextGeo();
		if (isAlgebraViewFocused) {
			setAlgebraViewAsFocusedPanel();
		}
		return selected;
	}

	/**
	 * Focuses the previous available GeoElement
	 * <p>
	 * If the Algebra View is currently focused, this method ensures that the focused panel,
	 * after the previous GeoElement has been selected, remains the AlgebraView.
	 * </p>
	 * @return whether the selection of the previous geo was successful
	 */
	@Override
	public boolean focusPrevious() {
		boolean isAlgebraViewFocused = app.isAlgebraViewFocused();
		selected = selectionManager.selectPreviousGeo();
		if (isAlgebraViewFocused) {
			setAlgebraViewAsFocusedPanel();
		}
		return selected;
	}

	private void setAlgebraViewAsFocusedPanel() {
		app.getGuiManager().getLayout().getDockManager().setFocusedPanel(App.VIEW_ALGEBRA);
	}

	@Override
	public AccessibilityGroup getAccessibilityGroup() {
		return AccessibilityGroup.GEO_ELEMENT;
	}

	@Override
	public AccessibilityGroup.ViewControlId getViewControlId() {
		return null;
	}

	public void setFocused(boolean b) {
		this.selected = b;
	}
}
