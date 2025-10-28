package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;

public class TextIconButton extends ToolIconButton {
	private final AppW appW;
	private final List<Integer> tools;
	private TextCategoryPopup textCategoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect buttons callback
	 */
	public TextIconButton(AppW appW, Runnable deselectButtons,
			List<Integer> tools) {
		super(tools.get(0), appW);
		this.appW = appW;
		this.tools = tools;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
			setActive(true);

			appW.setMode(textCategoryPopup.getLastSelectedMode());
			textCategoryPopup.getPopupPanel().addCloseHandler(e ->
					AriaHelper.setAriaExpanded(this, false));
		});
	}

	private void initPopupAndShow() {
		if (textCategoryPopup == null) {
			textCategoryPopup = new TextCategoryPopup(appW, this, tools);
			textCategoryPopup.getPopupPanel().setAutoHideEnabled(false);
		}

		showHidePopup();
	}

	private void showHidePopup() {
		if (getPopup().isShowing()) {
			getPopup().hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(getPopup(),
					this, appW);
		}

		AriaHelper.setAriaExpanded(this, getPopup().isShowing());
	}

	@Override
	public int getMode() {
		return textCategoryPopup != null ? textCategoryPopup.getLastSelectedMode()
				: tools.get(0);
	}

	@Override
	public boolean containsMode(int mode) {
		return tools.contains(mode);
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (textCategoryPopup != null) {
			textCategoryPopup.setLabels();
		}
	}

	private GPopupPanel getPopup() {
		return textCategoryPopup.getPopupPanel();
	}
}
