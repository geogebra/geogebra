package org.geogebra.common.main.settings.updater;

import java.util.Map;
import java.util.Set;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.settings.LabelSettings;
import org.geogebra.common.main.settings.LabelVisibility;

import com.google.j2objc.annotations.Weak;

/**
 * Updates the label settings.
 * Every complex (longer than 1 line) logic related to label settings
 * should be implemented in this class.
 */
public class LabelSettingsUpdater {

	@Weak
	private App app;
	@Weak
	private Construction construction;
	@Weak
	private LabelSettings labelSettings;

	LabelSettingsUpdater(App app) {
		this.app = app;
		construction = app.getKernel().getConstruction();
		labelSettings = app.getSettings().getLabelSettings();
	}

	/**
	 * Sets label visibility and sets the label mode of geo elements to default.
	 * @param visibility label visibility
	 */
	public void setLabelVisibility(LabelVisibility visibility) {
		labelSettings.setLabelVisibility(visibility);
		resetLabelModeToDefaultForGeos();
	}

	/**
	 * Resets the label mode of geo elements to default.
	 */
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

	/**
	 * Resets the menu's label visibility and updates the menu bar.
	 */
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
