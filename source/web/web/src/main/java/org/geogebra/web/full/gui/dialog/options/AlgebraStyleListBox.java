/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
