package org.geogebra.common.main.exam.restriction;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;

import org.geogebra.common.main.Localization;

public enum ExamRegion {
	GENERIC("") {
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			// no specific restrictions
		}

		@Override
		public String getDisplayName(Localization loc) {
			return loc.getMenu("Exam");
		}
	},
	NIEDERSACHSEN("Abitur Niedersachsen") {
		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setSubAppCodes(CAS_APPCODE, G3D_APPCODE);
		}
	};

	private final String displayName; // not localized; override getDisplayName instead if needed

	ExamRegion(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName(Localization loc) {
		return displayName;
	}

	public abstract void applyRestrictions(ExamRestrictionModel model);
}
