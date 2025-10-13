package org.geogebra.common.properties.impl.graphics;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.StringProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class DistanceBetweenEyesProperty extends AbstractValuedProperty<String>
		implements StringProperty, SettingsDependentProperty {
	private final EuclidianSettings3D euclidianSettings;

	/**
	 * Constructs a distance between eyes property used for
	 * {@link ProjectionsProperty} PROJECTION_GLASSES
	 * @param localization this is used to localize the name
	 * @param euclidianSettings euclidian settings
	 *
	 */
	public DistanceBetweenEyesProperty(Localization localization,
			EuclidianSettings3D euclidianSettings) {
		super(localization, "EyesSeparation");
		this.euclidianSettings = euclidianSettings;
	}

	@Override
	protected void doSetValue(String value) {
		if (validateValue(value) == null) {
			euclidianSettings.setEyeSep(Integer.parseInt(value));
		}
	}

	@Override
	public String getValue() {
		return String.valueOf(euclidianSettings.getEyeSep());
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
	public boolean isAvailable() {
		return euclidianSettings.getProjection() == EuclidianView3DInterface.PROJECTION_GLASSES;
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianSettings;
	}
}
