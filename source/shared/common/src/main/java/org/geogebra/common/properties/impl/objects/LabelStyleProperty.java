package org.geogebra.common.properties.impl.objects;

import java.util.List;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;

public class LabelStyleProperty extends FlagListProperty {
	private final Kernel kernel;
	private final GeoElement element;

	/**
	 * Constructs an angle unit property.
	 * @param localization localization
	 */
	public LabelStyleProperty(Localization localization, Kernel kernel, GeoElement element) {
		super(localization, "");
		this.kernel = kernel;
		this.element = element;
	}

	@Override
	public List<String> getFlagNames() {
		return List.of(getLocalization().getMenu("ShowLabel"),
				getLocalization().getMenu("ShowValue"));
	}

	@Override
	protected void doSetValue(List<Boolean> values) {
		boolean name = values.get(0);
		boolean value = values.get(1);
		int mode = -1;
		if (name && value) {
			mode = isForceCaption() ? GeoElementND.LABEL_CAPTION_VALUE
					: GeoElementND.LABEL_NAME_VALUE;
		} else if (name) {
			mode = isForceCaption() ? GeoElementND.LABEL_CAPTION
					: GeoElementND.LABEL_NAME;
		} else if (value) {
			mode = GeoElementND.LABEL_VALUE;
		}
		element.setLabelMode(mode);
		element.setLabelVisible(mode != -1);
		element.updateVisualStyle(GProperty.LABEL_STYLE);
		kernel.notifyRepaint();
	}

	@Override
	public List<Boolean> getValue() {
		if (!element.isLabelVisible()) {
			return List.of(false, false);
		}
		int labelStyle = element.getLabelMode();
		boolean showName = labelStyle == GeoElementND.LABEL_NAME
				|| labelStyle == GeoElementND.LABEL_CAPTION_VALUE
				|| labelStyle == GeoElementND.LABEL_NAME_VALUE
				|| labelStyle == GeoElementND.LABEL_CAPTION;
		boolean showValue = labelStyle == GeoElementND.LABEL_VALUE
				|| labelStyle == GeoElementND.LABEL_CAPTION_VALUE
				|| labelStyle == GeoElementND.LABEL_NAME_VALUE;
		return List.of(showName, showValue);
	}

	private boolean isForceCaption() {
		return !element
				.getLabel(StringTemplate.defaultTemplate)
				.equals(element.getCaption(StringTemplate.defaultTemplate));
	}
}
