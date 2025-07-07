package org.geogebra.web.full.gui.dialog.options;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.ListBox;

public class AlgebraStyleListBox extends ListBox {

	private final AppW app;
	private final boolean spreadsheet;
	private final List<AlgebraStyle> algebraStyles;

	/**
	 * Creates a ListBox for choosing algebra style.
	 * 
	 * @param appW
	 *            the application.
	 */
	public AlgebraStyleListBox(AppW appW, boolean spreadsheet0) {
		this.app = appW;
		this.spreadsheet = spreadsheet0;
		this.algebraStyles = AlgebraStyle.getAvailableValues(app);
		addChangeHandler(event -> {
			int index = getSelectedIndex();
			Kernel kernel = app.getKernel();

			if (spreadsheet) {
				kernel.setAlgebraStyleSpreadsheet(algebraStyles.get(index));
			} else {
				app.getSettings().getAlgebra().setStyle(algebraStyles.get(index));
			}
			kernel.updateConstruction(false);
		});

	}

	/**
	 * Updates listBox selection and texts (at language change)
	 */
	public void update() {
		clear();
		algebraStyles.forEach(style -> addItem(style.getTranslationKey()));
		setSelectedIndex(algebraStyles.indexOf(app.getAlgebraStyle()));
	}
}
