package org.geogebra.web.full.gui.toolbarpanel.spreadsheet.stylebar;

import org.geogebra.common.spreadsheet.core.SpreadsheetStyleBarModel;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class SpreadsheetStyleBar extends FlowPanel {
	private final AppW appW;
	private final SpreadsheetStyleBarModel styleBarModel;
	private IconButton backgroundColorButton;
	private IconButton fontColorButton;
	private IconButton boldButton;
	private IconButton italicButton;
	private IconButton horizontalAlignmentButton;
	private SpreadsheetStyleBarColorPopup backgroundColorPopup;
	private SpreadsheetStyleBarColorPopup fontColorPopup;
	private HorizontalAlignmentPopup horizontalAlignmentPopup;

	/**
	 * Spreadsheet style bar
	 * @param appW application
	 * @param styleBarModel model {@link SpreadsheetStyleBarModel}
	 * @param withDivider whether the style bar should add a divider at the start
	 */
	public SpreadsheetStyleBar(AppW appW, SpreadsheetStyleBarModel styleBarModel,
			boolean withDivider) {
		this.appW = appW;
		this.styleBarModel = styleBarModel;
		styleBarModel.stateChanged.addListener(this::updateState);
		decorateStyleBar();
		if (withDivider) {
			addDivider();
		}
		initStyleBar();
	}

	// UI related methods

	private void decorateStyleBar() {
		addStyleName("spreadsheetStyleBar");
		updateState(styleBarModel.getState());
	}

	private void initStyleBar() {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;

		backgroundColorButton = buildIconButton(res.color_black(), "stylebar.BgColor");
		backgroundColorButton.addFastClickHandler(source -> {
			toggleBackgroundColorPopup();
			backgroundColorButton.setActive(!backgroundColorButton.isActive());
		});

		fontColorButton = buildIconButton(res.text_color(), "stylebar.Color");
		fontColorButton.addFastClickHandler(source -> {
			toggleFontColorPopup();
			fontColorButton.setActive(!fontColorButton.isActive());
		});

		boldButton = buildIconButton(res.text_bold_black(), "Bold");
		boldButton.addFastClickHandler(source -> styleBarModel.setBold(!boldButton.isActive()));

		italicButton = buildIconButton(res.text_italic_black(), "Italic");
		italicButton.addFastClickHandler(source ->
				styleBarModel.setItalic(!italicButton.isActive()));

		horizontalAlignmentButton = buildIconButton(res.horizontal_align_right(),
				"stylebar.HorizontalAlign");
		horizontalAlignmentButton.addFastClickHandler(source -> {
			toggleAlignmentPopup();
			horizontalAlignmentButton.setActive(!horizontalAlignmentButton.isActive());
		});
	}

	private IconButton buildIconButton(SVGResource svgResource, String ariaLabel) {
		IconButton button = new IconButton(appW, () -> {},
				new ImageIconSpec(svgResource), appW.getLocalization().getMenu(ariaLabel));
		add(button);
		return button;
	}

	private void addDivider() {
		add(BaseWidgetFactory.INSTANCE.newDivider(true));
	}

	// State update

	private void updateState(SpreadsheetStyleBarModel.State newState) {
		for (Widget button : getChildren()) {
			if (button instanceof IconButton) {
				((IconButton) button).setDisabled(!newState.isEnabled);
			}
		}

		updateButtonState(newState);
	}

	private void updateButtonState(SpreadsheetStyleBarModel.State newState) {
		if (backgroundColorButton != null && backgroundColorPopup != null) {
			backgroundColorPopup.updateState(newState.backgroundColor);
		}
		if (fontColorButton != null && fontColorPopup != null) {
			fontColorPopup.updateState(newState.textColor);
		}
		if (boldButton != null) {
			boldButton.setActive(newState.fontTraits.contains(SpreadsheetStyling.FontTrait.BOLD));
		}
		if (italicButton != null) {
			italicButton.setActive(newState.fontTraits.contains(
					SpreadsheetStyling.FontTrait.ITALIC));
		}
		if (horizontalAlignmentButton != null && horizontalAlignmentPopup != null) {
			horizontalAlignmentPopup.updateState();
		}
	}

	// Horizontal alignment

	private void initAlignmentPopup() {
		if (horizontalAlignmentPopup == null) {
			horizontalAlignmentPopup = new HorizontalAlignmentPopup(appW,
					horizontalAlignmentButton, styleBarModel);
		}
	}

	private void toggleAlignmentPopup() {
		initAlignmentPopup();
		togglePopupVisibility(horizontalAlignmentPopup, horizontalAlignmentButton);
	}

	// Background color

	private void initBackgroundColorPopup() {
		if (backgroundColorPopup == null) {
			backgroundColorPopup = new SpreadsheetStyleBarColorPopup(appW, backgroundColorButton,
					styleBarModel::setBackgroundColor);
		}
	}

	private void toggleBackgroundColorPopup() {
		initBackgroundColorPopup();
		togglePopupVisibility(backgroundColorPopup, backgroundColorButton);
	}

	// Font color

	private void initFontColorPopup() {
		if (fontColorPopup == null) {
			fontColorPopup = new SpreadsheetStyleBarColorPopup(appW, fontColorButton,
					styleBarModel::setTextColor);
		}
	}

	private void toggleFontColorPopup() {
		initFontColorPopup();
		togglePopupVisibility(fontColorPopup, fontColorButton);
	}

	private void togglePopupVisibility(GPopupPanel popup, IconButton anchor) {
		if (popup.isShowing()) {
			popup.hide();
		} else {
			popup.showRelativeTo(anchor);
		}
	}
}
