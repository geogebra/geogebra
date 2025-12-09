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

package org.geogebra.web.full.gui.toolbar.mow.toolbox.text;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.toolbox.ToolboxIcon;

public class TextIconButton extends ToolIconButton {
	private final AppW appW;
	private final List<Integer> tools;
	private TextCategoryPopup textCategoryPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect buttons callback
	 * @param tools list of tools
	 */
	public TextIconButton(AppW appW, Runnable deselectButtons, List<Integer> tools) {
		super(tools.get(0), appW, appW.getToolboxIconResource()
				.getImageResource(ToolboxIcon.TEXTS), () -> {});
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
