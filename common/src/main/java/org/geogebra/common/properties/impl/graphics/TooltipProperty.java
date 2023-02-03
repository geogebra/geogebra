package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.impl.AbstractEnumerableProperty;

public class TooltipProperty extends AbstractEnumerableProperty {

	private int[] tooltipVisibility = {
			EuclidianStyleConstants.TOOLTIPS_ON,
			EuclidianStyleConstants.TOOLTIPS_AUTOMATIC,
			EuclidianStyleConstants.TOOLTIPS_OFF
	};

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
		setValues("On", "Automatic", "Off");
	}

	@Override
	protected void setValueSafe(String value, int index) {
		int mode = index;
		if (mode == 0) {
			mode = EuclidianStyleConstants.TOOLTIPS_ON;
		} else if (mode == 1) {
			mode = EuclidianStyleConstants.TOOLTIPS_AUTOMATIC;
		} else if (mode == 2) {
			mode = EuclidianStyleConstants.TOOLTIPS_OFF;
		}

		if (settings != null) {
			settings.setAllowToolTips(mode);
			return;
		}

		view.setAllowToolTips(mode);
	}

	@Override
	public int getIndex() {
		int tooltipStyle = settings.getAllowToolTips();
		for (int i = 0; i < tooltipVisibility.length; i++) {
			if (tooltipStyle == tooltipVisibility[i]) {
				return i;
			}
		}

		return -1;
	}
}
