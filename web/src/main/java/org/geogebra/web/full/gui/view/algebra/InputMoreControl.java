package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.util.StringUtil;

/**
 * Input control with more button
 */
public class InputMoreControl implements InputItemControl {

	private RadioTreeItem item;
	
	/**
	 * @param item
	 *            algebra view item
	 */
	public InputMoreControl(RadioTreeItem item) {
		this.item = item;
	}

	@Override
	public void ensureInputMoreMenu() {
		if (!item.isInputTreeItem()) {
			return;
		}

		item.controls.setMoreButtonVisible(!StringUtil.empty(item.getText()));
	}

	@Override
	public void hideInputMoreButton() {
		if (item.isInputTreeItem()) {
			item.controls.setMoreButtonVisible(false);
		}
	}
	
	@Override
	public void ensureControlVisibility() {
		if (item.controls == null) {
			return;
		}
		
		if (item.isInputTreeItem()) {
			item.controls.setMoreButtonVisible(false);
		} else {
			item.controls.setVisible(true);
		}
	}

	@Override
	public void addClearButtonIfSupported() {
		// no clear buttons here
	}

	@Override
	public void addInputControls() {
		item.addControls();
		item.controls.setMoreButtonVisible(false);
	}

	@Override
	public boolean hasMoreMenu() {
		return true;
	}
}