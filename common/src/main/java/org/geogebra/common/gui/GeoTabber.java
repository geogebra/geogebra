package org.geogebra.common.gui;

import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
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
		selectionManager.clearSelectedGeos(false);
		// selectNext / selectPrevious are responsible also for moving the focus -- they have
		// to decide if it goes to inputbox or canvas. We should not move focus here.
		if (reverse) {
			selected = selectionManager.selectPreviousGeo();
		} else {
			selected = selectionManager.selectNextGeo();
		}
		return selected;
	}

	@Override
	public boolean hasFocus() {
		return selected;
	}

	/**
	 * Focuses the next available GeoElement<br/>
	 * If the AlgebraView is currently focused, this method ensures that the focused panel,
	 * after the next GeoElement has been selected, remains the AlgebraView
	 * @return True if the selection of the next geo was successful, false else
	 */
	@Override
	public boolean focusNext() {
		boolean isAlgebraViewFocused = isAlgebraViewFocused();
		selected = selectionManager.selectNextGeo();
		if (isAlgebraViewFocused) {
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(App.VIEW_ALGEBRA);
		}
		return selected;
	}

	private boolean isAlgebraViewFocused() {
		GuiManagerInterface guiManager = app.getGuiManager();
		if (guiManager == null || guiManager.getLayout() == null
				|| guiManager.getLayout().getDockManager() == null) {
			return false;
		}
		return guiManager.getLayout().getDockManager().getFocusedViewId() == App.VIEW_ALGEBRA;
	}

	@Override
	public boolean focusPrevious() {
		selected = selectionManager.selectPreviousGeo();
		return selected;
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
