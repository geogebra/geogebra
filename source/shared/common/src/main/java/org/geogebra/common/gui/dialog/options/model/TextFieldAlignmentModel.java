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

package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

/**
 * Model for setting text field alignment.
 */
public class TextFieldAlignmentModel extends MultipleOptionsModel {

	/**
	 * Creates a new TextFieldAlignmentModel instance.
	 *
	 * @param app app
	 */
	public TextFieldAlignmentModel(App app) {
		super(app);
	}

	@Override
	public List<String> getChoices(Localization loc) {
		return Arrays.asList(loc.getMenu("stylebar.AlignLeft"),
				loc.getMenu("stylebar.AlignCenter"),
				loc.getMenu("stylebar.AlignRight"));
	}

	@Override
	public String getTitle() {
		return "stylebar.Align";
	}

	@Override
	protected void apply(int index, int value) {
		GeoInputBox inputBox = (GeoInputBox) getGeoAt(index);
		HorizontalAlignment alignment = HorizontalAlignment.values()[value];
		inputBox.setAlignment(alignment);
		inputBox.updateRepaint();
	}

	@Override
	protected int getValueAt(int index) {
		GeoInputBox inputBox = (GeoInputBox) getGeoAt(index);
		HorizontalAlignment alignment = inputBox.getAlignment();
		return alignment.ordinal();
	}

	@Override
	protected boolean isValidAt(int index) {
		return getGeoAt(index) instanceof GeoInputBox;
	}
}
