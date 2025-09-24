package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class ShowAxisNumbersProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final int axis;
	private EuclidianSettings euclidianSettings;
	private EuclidianViewInterfaceCommon euclidianView;

	/** Creates a property to show/hide axis numbers.
	 * @param localization localization
	 * @param axis axis index
	 * @param euclidianSettings euclidian settings
	 * @param euclidianView euclidian view
	 */
	public ShowAxisNumbersProperty(Localization localization, int axis,
			EuclidianSettings euclidianSettings, EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "ShowAxisNumbers");
		this.axis = axis;
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
	}

	@Override
	protected void doSetValue(Boolean value) {
		euclidianSettings.setShowAxisNumbers(axis, value);
		euclidianView.updateBackground();
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.getShowAxisNumbers()[axis];
	}
}
