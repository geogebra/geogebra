package org.geogebra.common.properties.impl.graphics;

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;

/**
 * This property controls the tick style of axis.
 */
public class AxisTickProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private EuclidianSettings euclidianSettings;
	private final int axis;
	private EuclidianViewInterfaceCommon euclidianView;

	private PropertyResource[] icons = new PropertyResource[]{
			PropertyResource.ICON_AXIS_TICK_MAJOR, PropertyResource.ICON_AXIS_TICK_MAJOR_AND_MINOR,
			PropertyResource.ICON_AXIS_TICK_OFF};

	/**
	 * Controls a tick style property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param axis axis index
	 * @param euclidianView euclidian view
	 */
	public AxisTickProperty(Localization localization, EuclidianSettings euclidianSettings,
			int axis, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "AxisTicks");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
		this.axis = axis;
		setValues(List.of(EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_MAJOR_MINOR,
				EuclidianStyleConstants.AXES_TICK_STYLE_NONE));
	}

	@Override
	public Integer getValue() {
		return euclidianSettings.getAxesTickStyles()[axis];
	}

	@Override
	protected void doSetValue(Integer value) {
		euclidianSettings.setAxisTickStyle(axis, value);
		euclidianView.updateBackground();
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getLabels() {
		return null;
	}
}
