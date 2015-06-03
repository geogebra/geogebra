package org.geogebra.common.gui.dialog.options.model;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.main.Localization;

public class TooltipModel extends MultipleOptionsModel {

	@Override
	public List<String> getChoiches(Localization loc) {
		return Arrays.asList(loc.getMenu("Labeling.automatic"), // index 0
				loc.getMenu("on"), // index 1
				loc.getMenu("off"), // index 2
				loc.getPlain("Caption"), // index 3
				loc.getPlain("NextCell") // index 4 Michael Borcherds
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
