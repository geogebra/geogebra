package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import static org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxMow.TOOLBOX_PADDING;

import org.geogebra.web.full.css.MaterialDesignResources;
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

		textCategoryPopup.showAtPoint(getAbsoluteLeft() + getOffsetWidth() + TOOLBOX_PADDING,
				(int) (getAbsoluteTop() - appW.getAbsTop()));
		AriaHelper.setAriaExpanded(this, true);

		textCategoryPopup.getPopupPanel().addCloseHandler(e ->
				AriaHelper.setAriaExpanded(this, false));
	}

	@Override
	public void setLabels() {
		super.setLabels();
		textCategoryPopup.setLabels();
	}
}
