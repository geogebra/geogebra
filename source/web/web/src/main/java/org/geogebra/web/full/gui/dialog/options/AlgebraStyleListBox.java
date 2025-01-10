package org.geogebra.web.full.gui.dialog.options;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.ListBox;

public class AlgebraStyleListBox extends ListBox {
	private AppW app;
	private boolean spreadsheet;

	/**
	 * Creates a ListBox for choosing algebra style.
	 * 
	 * @param appW
	 *            the application.
	 */
	public AlgebraStyleListBox(AppW appW, boolean spreadsheet0) {
		this.app = appW;
		this.spreadsheet = spreadsheet0;
		addChangeHandler(event -> {
			int idx = getSelectedIndex();
			Kernel kernel = app.getKernel();

			if (spreadsheet) {
				kernel.setAlgebraStyleSpreadsheet(
						AlgebraSettings.getStyleModeAt(idx));
			} else {
				kernel.setAlgebraStyle(
						AlgebraSettings.getStyleModeAt(idx));
			}

			kernel.updateConstruction(false);
		});

	}

	/**
	 * Updates listBox selection and texts (at language change)
	 */
	public void update() {
		String[] modes = AlgebraSettings.getDescriptionModes(app);
		clear();

		for (int i = 0; i < modes.length; i++) {
			addItem(app.getLocalization().getMenu(modes[i]));
		}

		int descMode = app.getKernel().getAlgebraStyle();
		setSelectedIndex(AlgebraSettings.indexOfStyleMode(descMode));

	}
}
