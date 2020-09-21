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
