package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.util.StringUtil;

public class InputMoreControl implements InputItemControl {

	private RadioTreeItem item;
	
	public InputMoreControl(RadioTreeItem item) {
		this.item = item;
	}

	@Override
	public void ensureInputMoreMenu() {
		if (!item.isInputTreeItem()) {
			return;
		}

		if (StringUtil.empty(item.getText())) {
			item.controls.hideMoreButton();
		} else {
			item.controls.showMoreButton();
		}		
	}

	@Override
	public void hideInputMoreButton() {
		if (item.isInputTreeItem()) {
			item.controls.hideMoreButton();	
		}
	}
	
	@Override
	public void ensureControlVisibility() {
		if (item.controls == null) {
			return;
		}
		
		if (item.isInputTreeItem()) {
			item.controls.hideMoreButton();
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
		item.controls.hideMoreButton();
	}

	@Override
	public boolean hasMoreMenu() {
		return true;
	}
}