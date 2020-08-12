package org.geogebra.common.gui.dialog.options.model;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasDynamicCaption;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.StringUtil;

public class DynamicCaptionModel extends CommonOptionsModel<String> {

	private final Construction construction;
	private final List<String> choices;
	private final Kernel kernel;

	public DynamicCaptionModel(App app) {
		super(app);
		kernel = app.getKernel();
		construction = kernel.getConstruction();
		choices = new ArrayList<>();
	}

	@Override
	public List<String> getChoices(Localization loc) {
		choices.clear();
		choices.add("");
		for (GeoElement geo: construction.getGeoSetConstructionOrder()) {
			if (geo.isGeoText()) {
				choices.add(geo.getLabelSimple());
			}
		}
		return choices;
	}

	@Override
	protected void apply(int index, String value) {
		HasDynamicCaption geo = (HasDynamicCaption) getGeoAt(index);
		if (StringUtil.empty(value)) {
			geo.clearDynamicCaption();
		} else {
			GeoText caption = (GeoText) kernel.lookupLabel(value);
			geo.setDynamicCaption(caption);
		}
		geo.updateRepaint();
	}

	@Override
	protected String getValueAt(int index) {
		return choices.get(index);
	}

	@Override
	protected boolean isValidAt(int index) {
		GeoElement geo = getGeoAt(index);
		return geo instanceof HasDynamicCaption
				&& ((HasDynamicCaption) geo).hasDynamicCaption();
	}

	@Override
	public void updateProperties() {
		GeoText caption = ((GeoInputBox) getGeoAt(0)).getDynamicCaption();
		if (caption == null) {
			return;
		}

		String textLabel = caption.getLabelSimple();
		int index = StringUtil.empty(textLabel) ? 0 :getChoices(app.getLocalization()).indexOf(textLabel);
		getListener().setSelectedIndex(index);
	}
}
