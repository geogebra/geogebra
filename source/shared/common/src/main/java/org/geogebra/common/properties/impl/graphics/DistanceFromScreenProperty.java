package org.geogebra.common.properties.impl.graphics;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class DistanceFromScreenProperty extends AbstractValuedProperty<String>
		implements StringProperty {
	private EuclidianSettings3D euclidianSettings;

	/**
	 * Creates a distance from screen property used for
	 * {@link ProjectionsProperty} PROJECTION_PERSPECTIVE
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 */
	public DistanceFromScreenProperty(Localization localization,
			EuclidianSettings3D euclidianSettings) {
		super(localization, "EyeDistance");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(String value) {
		if (validateValue(value) == null) {
			euclidianSettings.setProjectionPerspectiveEyeDistance(Integer.parseInt(value));
		}
	}

	@Override
	public String getValue() {
		return String.valueOf(euclidianSettings.getProjectionPerspectiveEyeDistance());
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		try {
			Integer.parseInt(value);
			return null;
		} catch (Exception e) {
			return getLocalization().getError("InputError.Enter_a_number");
		}
	}

	@Override
	public boolean isEnabled() {
		return euclidianSettings.getProjection() == EuclidianView3DInterface.PROJECTION_PERSPECTIVE;
	}
}
