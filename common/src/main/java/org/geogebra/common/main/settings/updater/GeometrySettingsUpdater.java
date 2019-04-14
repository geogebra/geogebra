package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.LabelVisibility;

public class GeometrySettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsOnlyOnAppStart() {
		super.resetSettingsOnlyOnAppStart();
		getLabelSettingsUpdater().setLabelVisibility(LabelVisibility.PointsOnly);
		getSettings().getAlgebra().setStyle(AlgebraStyle.Description);
	}
}
