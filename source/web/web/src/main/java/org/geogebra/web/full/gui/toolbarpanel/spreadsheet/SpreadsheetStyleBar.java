package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetStyleBarModel;
import org.geogebra.common.spreadsheet.style.SpreadsheetStyling;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class SpreadsheetStyleBar extends FlowPanel {
	private final AppW appW;
	private final SpreadsheetStyleBarModel styleBarModel;
	private IconButton bold;
	private IconButton italic;

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

	private void initStyleBar() {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;

		bold = buildIconButton(res.text_bold_black(), "Bold");
		bold.addFastClickHandler(source -> styleBarModel.setBold(!bold.isActive()));

		italic = buildIconButton(res.text_italic_black(), "Italic");
		italic.addFastClickHandler(source -> styleBarModel.setItalic(!italic.isActive()));
	}

	private IconButton buildIconButton(SVGResource svgResource, String ariaLabel) {
		IconButton button = new IconButton(appW, () -> {},
				new ImageIconSpec(svgResource), appW.getLocalization().getMenu(ariaLabel));
		add(button);
		return button;
	}

	private void decorateStyleBar() {
		addStyleName("spreadsheetStyleBar");
		updateState(styleBarModel.getState());
	}

	private void addDivider() {
		add(BaseWidgetFactory.INSTANCE.newDivider(true));
	}

	private void updateState(SpreadsheetStyleBarModel.State newState) {
		for (Widget button : getChildren()) {
			if (button instanceof IconButton) {
				((IconButton) button).setDisabled(!newState.isEnabled);
			}
		}

		updateButtonState(newState);
	}

	private void updateButtonState(SpreadsheetStyleBarModel.State newState) {
		if (bold != null) {
			bold.setActive(newState.fontTraits.contains(SpreadsheetStyling.FontTrait.BOLD));
		}
		if (italic != null) {
			italic.setActive(newState.fontTraits.contains(SpreadsheetStyling.FontTrait.ITALIC));
		}
	}
}
