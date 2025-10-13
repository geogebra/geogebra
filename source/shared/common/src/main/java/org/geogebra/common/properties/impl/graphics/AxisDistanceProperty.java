package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.NumericPropertyWithSuggestions;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * This property controls the distance of an axis numbering
 */
public class AxisDistanceProperty extends NumericPropertyWithSuggestions
		implements SettingsDependentProperty {
	private final EuclidianSettings euclidianSettings;
	private final EuclidianViewInterfaceCommon euclidianView;
	private final int axis;

	/**
	 * Constructs an xAxis property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView the active euclidian view
	 * @param kernel kernel
	 * @param label label of the axis
	 * @param axis the axis for the numbering distance will be set
	 */
	public AxisDistanceProperty(Localization localization, EuclidianSettings
			euclidianSettings, EuclidianViewInterfaceCommon euclidianView,
			Kernel kernel, String label, int axis) {
		super(kernel.getAlgebraProcessor(), localization, label);
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		euclidianSettings.setAxisNumberingDistance(axis, value);
	}

	@Override
	protected NumberValue getNumberValue() {
		GeoNumberValue numberValue = euclidianSettings.getAxisNumberingDistance(axis);
		if (numberValue != null) {
			return numberValue;
		}
		return euclidianView.getAxesDistanceObjects()[axis];
	}

	@Override
	public boolean isEnabled() {
		return !euclidianSettings.getAutomaticAxesNumberingDistances()[axis];
	}

	@Override
	public List<String> getSuggestions() {
		return List.of("1", Unicode.PI_STRING, Unicode.PI_HALF_STRING);
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
