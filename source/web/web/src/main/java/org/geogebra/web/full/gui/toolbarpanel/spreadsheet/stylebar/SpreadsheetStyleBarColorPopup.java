package org.geogebra.web.full.gui.toolbarpanel.spreadsheet.stylebar;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.color.ColorValues;
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
		colorChooserPanel = new ColorChooserPanel((AppW) getApplication(), Arrays.stream(
				GeoColorValues.values()).map(ColorValues::getColor).collect(Collectors.toList()),
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
