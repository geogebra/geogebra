package org.geogebra.common.main.exam.restriction;

import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;

public enum ExamRegion {
	GENERIC() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return loc.getMenu(config.getAppTransKey());
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			String shortAppName = config.getAppCode().equals(GeoGebraConstants.SUITE_APPCODE)
					? GeoGebraConstants.SUITE_SHORT_NAME
					: config.getAppNameShort();
			return loc.getMenu(shortAppName);
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// no specific restrictions
		}
	},
	NIEDERSACHSEN() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Niedersachsen Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Niedersachsen";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setSubAppCodes(G3D_APPCODE);
		}
	};

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	public abstract void applyRestrictions(ExamRestrictionModel model);
}
