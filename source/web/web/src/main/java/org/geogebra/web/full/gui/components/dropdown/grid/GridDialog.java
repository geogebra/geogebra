package org.geogebra.web.full.gui.components.dropdown.grid;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.common.properties.impl.graphics.RulingStyleProperty;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

public class GridDialog extends ComponentDialog {
	private BackgroundType selectedRuling;
	private final RulingStyleProperty rulingStyleProperty;

	/**
	 * base dialog constructor
	 * @param app see {@link AppW}
	 * @param dialogData contains trans keys for title and buttons
	 */
	public GridDialog(AppW app, DialogData dialogData) {
		super(app, dialogData, true, true);
		addStyleName("rulingDialog");
		setOnPositiveAction(this::applyRuling);

		rulingStyleProperty = new RulingStyleProperty(app.getLocalization(),
				app.getActiveEuclidianView().getSettings(), app.getActiveEuclidianView());
		selectedRuling = rulingStyleProperty.getValue();
		buildGui();
	}

	private void buildGui() {
		GridCardPanel cardPanel = new GridCardPanel((AppW) app, selectedRuling);
		cardPanel.setListener((index) -> selectedRuling = BackgroundType.rulingOptions.get(index));
		addDialogContent(cardPanel);
	}

	private void applyRuling() {
		if (rulingStyleProperty.getValue() != selectedRuling) {
			rulingStyleProperty.setValue(selectedRuling);
			app.storeUndoInfo();
		}
	}
}
