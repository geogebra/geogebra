package org.geogebra.web.full.gui.toolbarpanel.spreadsheet;

import org.geogebra.common.spreadsheet.core.SpreadsheetStyleBarModel;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

public class SpreadsheetStyleBar extends FlowPanel {
	private final AppW appW;
	private final SpreadsheetStyleBarModel styleBarModel;

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
		IconButton button = new IconButton(appW, () -> {}, new ImageIconSpec(
				MaterialDesignResources.INSTANCE.color_black()), "dummy");
		add(button);
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
	}
}
