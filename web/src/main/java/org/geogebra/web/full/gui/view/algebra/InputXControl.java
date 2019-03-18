package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.NoDragImage;

public class InputXControl implements InputItemControl {

	private RadioTreeItem item;

	public InputXControl(RadioTreeItem item) {
		this.item = item;
	}

	@Override
	public void ensureInputMoreMenu() {
		// nothing to do
	}

	@Override
	public void hideInputMoreButton() {
		// nothing to do
		
	}

	@Override
	public void ensureControlVisibility() {
		if (item.controls == null) {
			return;
		}
		
		item.controls.setVisible(true);
	}

	@Override
	public void addClearButtonIfSupported() {
		item.content.insert(getClearInputButton(), 0);
		
	}

	private GPushButton getClearInputButton() {
		if (item.btnClearInput == null) {
			item.btnClearInput = new GPushButton(
					new NoDragImage(MaterialDesignResources.INSTANCE.clear(), 24));
			ClickStartHandler.init(item.btnClearInput,
					new ClickStartHandler(false, true) {

						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {
							item.clearInput();
							item.getController().setFocus(true);

						}
					});
			item.btnClearInput.addStyleName("ggb-btnClearAVInput");
		}
		return item.btnClearInput;
	}

	@Override
	public void addInputControls() {
		// not needed		
	}

	@Override
	public boolean hasMoreMenu() {
		return !item.isInputTreeItem();
	}

}
