package org.geogebra.web.web.gui.dialog.options;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.settings.AlgebraSettings;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class AlgebraStyleListBox extends ListBox {
	private AppW app;

	/**
	 * Creates a ListBox for choosing algebra style.
	 * 
	 * @param appW
	 *            the application.
	 */
	public AlgebraStyleListBox(AppW appW) {
		this.app = appW;
		addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				int idx = getSelectedIndex();
				if (app.has(Feature.AV_DEFINITION_AND_VALUE)) {
					app.getKernel().setAlgebraStyle(
							AlgebraSettings.getStyleModeAt(idx));
				} else {
					app.getKernel().setAlgebraStyle(idx);

				}

				app.getKernel().updateConstruction();
			}

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
		if (app.has(Feature.AV_DEFINITION_AND_VALUE)) {
			setSelectedIndex(AlgebraSettings.indexOfStyleMode(descMode));
		} else {
			setSelectedIndex(descMode);
		}

	}
}
