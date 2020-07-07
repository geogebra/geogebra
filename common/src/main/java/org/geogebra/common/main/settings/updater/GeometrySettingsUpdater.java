package org.geogebra.common.main.settings.updater;

import org.geogebra.common.main.settings.AlgebraStyle;
import org.geogebra.common.main.settings.LabelVisibility;

/**
 * Sets the geometry settings.
 */
public class GeometrySettingsUpdater extends SettingsUpdater {

	@Override
	public void resetSettingsOnlyOnAppStart() {
		super.resetSettingsOnlyOnAppStart();
		getSettings().getAlgebra().setStyle(AlgebraStyle.DESCRIPTION);
	}

	@Override
	public void resetSettingsAfterClearAll() {
		super.resetSettingsAfterClearAll();
		getLabelSettingsUpdater().setLabelVisibility(LabelVisibility.PointsOnly);
	}
}
