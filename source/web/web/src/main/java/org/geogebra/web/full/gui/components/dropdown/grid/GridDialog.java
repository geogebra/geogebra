package org.geogebra.web.full.gui.components.dropdown.grid;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

public class GridDialog extends ComponentDialog {
	private final EuclidianView view;
	private BackgroundType selectedRuling;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 * @param view - euclidian view
	 */
	public GridDialog(AppW app, DialogData dialogData, EuclidianView view) {
		super(app, dialogData, true, true);
		addStyleName("rulingDialog");
		setOnPositiveAction(this::applyRuling);

		this.view = view;
		selectedRuling = view.getSettings().getBackgroundType();
		buildGui();
	}

	private void buildGui() {
		GridCardPanel cardPanel = new GridCardPanel((AppW) app, selectedRuling);
		cardPanel.setListener((index) -> selectedRuling = BackgroundType.rulingOptions.get(index));
		addDialogContent(cardPanel);
	}

	private void applyRuling() {
		EuclidianSettings settings = view.getSettings();
		if (settings.getBackgroundType() != selectedRuling) {
			settings.setBackgroundType(selectedRuling);

			view.updateBackground();
			app.storeUndoInfo();
		}
	}
}
