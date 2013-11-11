package geogebra.common.gui.dialog.options.model;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.Localization;

import java.util.Arrays;
import java.util.List;

public class TooltipModel extends MultipleOptionsModel {
	public TooltipModel(IComboListener listener) {
		super(listener);
	}

	@Override
	public void updateProperties() {
		GeoElement geo0 = getGeoAt(0);
		boolean equalLabelMode = true;

		for (int i = 1; i < getGeosLength(); i++) {
			if (geo0.getLabelMode() != getGeoAt(i).getTooltipMode())
				equalLabelMode = false;

		}

		updateComboSelection(equalLabelMode, geo0.getTooltipMode());
	}

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


}
