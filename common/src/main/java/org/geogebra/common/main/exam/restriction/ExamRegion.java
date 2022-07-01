package org.geogebra.common.main.exam.restriction;

import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;

import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;

public enum ExamRegion {
	GENERIC("") {
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// no specific restrictions
		}

		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return loc.getMenu(config.getAppTransKey());
		}
	},
	NIEDERSACHSEN("Niedersachsen Abitur") {
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setSubAppCodes(G3D_APPCODE);
		}
	};

	private final String displayName; // not localized; override getDisplayName instead if needed

	ExamRegion(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName(Localization loc, AppConfig config) {
		return displayName;
	}

	public abstract void applyRestrictions(ExamRestrictionModel model);
}
