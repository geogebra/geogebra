package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;

public class TooltipModel extends MultipleOptionsModel {

	@Override
	public String getTitle() {
		return "Tooltip";
	}

	public TooltipModel(App app) {
		super(app);
	}

	@Override
	public List<String> getChoices(Localization loc) {
		return Arrays.asList(loc.getMenu("Labeling.automatic"), // index 0
				loc.getMenu("On"), // index 1
				loc.getMenu("Off"), // index 2
				loc.getMenu("Caption"), // index 3
				loc.getMenu("NextCell") // index 4
		);

	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isDrawable();
	}

	@Override
	public void apply(int index, int value) {
		getGeoAt(index).setTooltipMode(value);
	}

	@Override
	public int getValueAt(int index) {
		return getGeoAt(index).getTooltipMode();
	}

}
