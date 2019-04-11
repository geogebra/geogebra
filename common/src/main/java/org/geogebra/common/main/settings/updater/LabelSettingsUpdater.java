package org.geogebra.common.main.settings.updater;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.settings.LabelSettings;
import org.geogebra.common.main.settings.LabelVisibility;

import java.util.Map;
import java.util.Set;

public class LabelSettingsUpdater {

	private App app;
	private Construction construction;
	private LabelSettings labelSettings;

	LabelSettingsUpdater(App app) {
		this.app = app;
		construction = app.getKernel().getConstruction();
		labelSettings = app.getSettings().getLabelSettings();
	}

	public void setLabelVisibility(LabelVisibility visibility) {
		labelSettings.setLabelVisibility(visibility);
		resetLabelModeToDefaultForGeos();
	}

	public void resetLabelModeToDefaultForGeos() {
		Set<Map.Entry<Integer, GeoElement>> defaultGeos =
				construction.getConstructionDefaults().getDefaultGeos();
		for (Map.Entry<Integer, GeoElement> entry : defaultGeos) {
			GeoElement geo = entry.getValue();
			if (!construction.getApplication().isUnbundledOrWhiteboard()
					|| !(geo instanceof GeoAngle)) {
				geo.labelMode = GeoElementND.LABEL_DEFAULT;
			}
			geo.setLabelVisible(true);
		}
	}

	public void resetLabelVisibilityForMenu() {
		labelSettings.resetLabelVisibilityForMenu();
		updateMenubar();
	}

	private void updateMenubar() {
		GuiManagerInterface guiManager = app.getGuiManager();
		if (guiManager != null) {
			guiManager.updateMenubar();
		}
	}
}
