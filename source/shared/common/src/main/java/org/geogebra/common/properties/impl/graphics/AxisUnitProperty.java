package org.geogebra.common.properties.impl.graphics;

import java.util.Arrays;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * This property controls the unit of axis
 */
public class AxisUnitProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions, SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final EuclidianViewInterfaceCommon euclidianView;
	private final int axis;

	/**
	 * Constructs an axis unit property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView the active euclidian view
	 * @param axis the axis for the numbering distance will be set
	 */
	public AxisUnitProperty(Localization localization, EuclidianSettings euclidianSettings,
			EuclidianViewInterfaceCommon euclidianView, int axis) {
		super(localization, "AxisUnitLabel");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return "";
	}

	@Override
	protected void doSetValue(String value) {
		euclidianSettings.setAxisUnitLabel(axis, value);
	}

	@Override
	public String getValue() {
		return euclidianSettings.getAxesUnitLabels()[axis];
	}

	@Override
	public boolean isEnabled() {
		return euclidianView.getShowAxesNumbers()[axis];
	}

	@Override
	public List<String> getSuggestions() {
		return Arrays.asList("",
				Unicode.DEGREE_STRING, // degrees
				Unicode.PI_STRING, // pi
				"mm",
				"cm",
				"m",
				"km",
				Unicode.CURRENCY_DOLLAR + "");
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
