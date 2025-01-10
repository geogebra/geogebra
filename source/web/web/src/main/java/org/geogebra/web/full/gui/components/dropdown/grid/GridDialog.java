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
	 */
	public GridDialog(AppW app, DialogData dialogData, EuclidianView view) {
		super(app, dialogData, true, true);
		addStyleName("rulingDialog");
		setOnPositiveAction(this::applyRuling);
		this.view = view;
		buildGui();
	}

	private void buildGui() {
		GridPopup grid = new GridPopup((AppW) app, null, 4);
		for (BackgroundType type : BackgroundType.rulingOptions) {
			grid.addItem(GridDataProvider.getTransKeyForRulingType(type),
					GridDataProvider.getResourceForBackgroundType(type));
		}

		selectedRuling = view.getSettings().getBackgroundType();
		grid.setSelectedIndex(BackgroundType.rulingOptions.indexOf(selectedRuling));
		grid.updateGui();

		grid.setListener((index) -> selectedRuling = BackgroundType.rulingOptions.get(index));
		addDialogContent(grid.getView());
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
