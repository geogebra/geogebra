package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;

public class TextIconButton extends IconButton {
	private AppW appW;
	private TextCategoryPopup textCategoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect buttons callback
	 */
	public TextIconButton(AppW appW, Runnable deselectButtons) {
		super(appW, MaterialDesignResources.INSTANCE.texts(), "Text.Tool", "Text.Tool",
				"", null);
		this.appW = appW;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
			setActive(true);
			appW.setMode(textCategoryPopup.getLastSelectedMode());
		});
	}

	private void initPopupAndShow() {
		if (textCategoryPopup == null) {
			textCategoryPopup = new TextCategoryPopup(appW, this);
		}
		ToolboxPopupPositioner.showRelativeToToolbox(textCategoryPopup.getPopupPanel(),
				this, appW);
		AriaHelper.setAriaExpanded(this, true);

		textCategoryPopup.getPopupPanel().addCloseHandler(e ->
				AriaHelper.setAriaExpanded(this, false));
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (textCategoryPopup != null) {
			textCategoryPopup.setLabels();
		}
	}
}
