package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;

public class LayerModel extends MultipleOptionsModel {

	public LayerModel(App app) {
		super(app);
	}

	@Override
	public boolean isValidAt(int index) {
		return getGeoAt(index).isDrawable();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		List<String> choices = new ArrayList<>();
		for (int layer = 0; layer <= EuclidianStyleConstants.MAX_LAYERS; ++layer) {
			choices.add(" " + layer);
		}
		return choices;
	}

	@Override
	protected void apply(int index, int value) {
		GeoElement geo = getGeoAt(index);
		geo.setLayer(value);
		geo.updateVisualStyleRepaint(GProperty.LAYER);
	}

	@Override
	public int getValueAt(int index) {
		return getGeoAt(index).getLayer();
	}

}
