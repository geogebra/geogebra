package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class UseLightingBooleanProperty extends AbstractValuedProperty<Boolean>
	implements BooleanProperty {
	private final EuclidianSettings3D euclidianSettings;
	private final EuclidianView euclidianView;

	/**
	 * Creates a property to use lighting
	 * @param localization localization
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public UseLightingBooleanProperty(Localization localization,
			EuclidianSettings3D euclidianSettings, EuclidianView euclidianView) {
		super(localization, "UseLighting");
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getUseLight();
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setUseLight(value);
		euclidianView.repaintView();
	}
}
