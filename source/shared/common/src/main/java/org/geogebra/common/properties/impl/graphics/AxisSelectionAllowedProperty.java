package org.geogebra.common.properties.impl.graphics;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.aliases.BooleanProperty;
import org.geogebra.common.properties.impl.AbstractValuedProperty;

public class AxisSelectionAllowedProperty extends AbstractValuedProperty<Boolean>
		implements BooleanProperty {
	private final int axisIndex;
	private EuclidianSettings euclidianSettings;
	private EuclidianViewInterfaceCommon euclidianView;

	/**
	 * Constructs the selection allowed property for axes.
	 * @param localization localization for the name
	 * @param euclidianSettings euclidian settings
	 * @param index axis index
	 * @param euclidianView euclidian view
	 */
	public AxisSelectionAllowedProperty(Localization localization,
			EuclidianSettings euclidianSettings, int index,
			EuclidianViewInterfaceCommon euclidianView) {
		super(localization, "SelectionAllowed");
		this.axisIndex = index;
		this.euclidianSettings = euclidianSettings;
		this.euclidianView = euclidianView;
	}

	@Override
	public Boolean getValue() {
		return euclidianSettings.isSelectionAllowed(axisIndex);
	}

	@Override
	public void doSetValue(Boolean value) {
		euclidianSettings.setSelectionAllowed(axisIndex, value);
		euclidianView.updateBackground();
	}
}
