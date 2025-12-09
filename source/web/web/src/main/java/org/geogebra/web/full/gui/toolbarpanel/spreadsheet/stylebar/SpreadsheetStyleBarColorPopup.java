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

package org.geogebra.web.full.gui.toolbarpanel.spreadsheet.stylebar;

import java.util.function.Consumer;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.color.GeoColorValues;
import org.geogebra.web.full.gui.toolbar.mow.popupcomponents.ColorChooserPanel;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.logical.shared.CloseEvent;
import org.gwtproject.event.logical.shared.CloseHandler;

public class SpreadsheetStyleBarColorPopup extends GPopupPanel
		implements CloseHandler<GPopupPanel> {
	private final IconButton anchorButton;
	private ColorChooserPanel colorChooserPanel;

	/**
	 * Color panel chooser for spreadsheet style bar.
	 * @param appW {@link AppW}
	 * @param anchorButton anchor button of popup
	 */
	public SpreadsheetStyleBarColorPopup(AppW appW, IconButton anchorButton,
			Consumer<GColor> colorHandler) {
		super(true, appW.getAppletFrame(), appW);
		this.anchorButton = anchorButton;
		addStyleName("quickStyleBarPopup colorStyle");
		buildGui(colorHandler);
		addCloseHandler(this);
	}

	private void buildGui(Consumer<GColor> colorHandler) {
		colorChooserPanel = new ColorChooserPanel((AppW) getApplication(),
				GeoColorValues.values(),
				color -> {
					colorHandler.accept(color);
					hide();
				});
		add(colorChooserPanel);
	}

	/**
	 * Update ui based on styleBarModel state
	 */
	public void updateState(GColor color) {
		colorChooserPanel.updateColorSelection(color);
	}

	@Override
	public void onClose(CloseEvent<GPopupPanel> event) {
		anchorButton.setActive(false);
	}
}
