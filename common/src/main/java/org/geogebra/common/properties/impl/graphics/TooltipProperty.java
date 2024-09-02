package org.geogebra.common.properties.impl.graphics;

import static java.util.Map.entry;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;

public class TooltipProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private EuclidianSettings settings;
	private EuclidianView view;

	/**
	 * tooltip property
	 * @param localization - localization
	 * @param settings - euclidian settings
	 * @param view - euclidian view
	 */
	public TooltipProperty(Localization localization, EuclidianSettings settings,
			EuclidianView view) {
		super(localization, "Labeling");
		this.settings = settings;
		this.view = view;
		setNamedValues(List.of(
				entry(EuclidianStyleConstants.TOOLTIPS_ON, "On"),
				entry(EuclidianStyleConstants.TOOLTIPS_AUTOMATIC, "Automatic"),
				entry(EuclidianStyleConstants.TOOLTIPS_OFF, "Off")
		));
	}

	@Override
	public Integer getValue() {
		return settings.getAllowToolTips();
	}

	@Override
	protected void doSetValue(Integer value) {
		if (settings != null) {
			settings.setAllowToolTips(value);
			return;
		}
		view.setAllowToolTips(value);
	}
}
