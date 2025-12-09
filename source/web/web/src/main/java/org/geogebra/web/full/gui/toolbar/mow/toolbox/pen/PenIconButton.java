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

package org.geogebra.web.full.gui.toolbar.mow.toolbox.pen;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PEN;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.ToolIconButton;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;

public class PenIconButton extends ToolIconButton {
	private final AppW appW;
	private PenCategoryPopup penPopup;
	private final List<Integer> modes;

	/**
	 * Constructor
	 * @param appW - application
	 * @param deselectButtons - deselect other button callback
	 */
	public PenIconButton(AppW appW, List<Integer> modes, Runnable deselectButtons) {
		super(MODE_PEN, appW);
		this.appW = appW;
		this.modes = modes;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			showPopup();
			setActive(true);

			AriaHelper.setAriaExpanded(this, true);
		});
	}

	private void showPopup() {
		appW.setMode(getMode());
		if (penPopup == null) {
			penPopup = new PenCategoryPopup(appW, modes, this::updateButton);
			penPopup.setAutoHideEnabled(false);
			penPopup.addCloseHandler((e) -> AriaHelper.setAriaExpanded(this, false));
		}
		penPopup.update();
		if (penPopup.isShowing()) {
			penPopup.hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(penPopup, this, appW);
		}
	}

	private void updateButton(int mode) {
		IconSpec icon = getIconFromMode(mode, appW.getToolboxIconResource());
		updateImgAndTxt(icon, mode, appW);
		setActive(true);
		if (penPopup != null) {
			penPopup.update();
		}
	}

	@Override
	public int getMode() {
		return penPopup == null || penPopup.getLastSelectedMode() == -1
				? MODE_PEN : penPopup.getLastSelectedMode();
	}

	@Override
	public boolean containsMode(int mode) {
		return modes.contains(mode);
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (penPopup != null) {
			penPopup.setLabels();
		}
	}
}
