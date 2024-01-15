package org.geogebra.common.main.exam;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GEOMETRY_APPCODE;
import static org.geogebra.common.GeoGebraConstants.GRAPHING_APPCODE;
import static org.geogebra.common.GeoGebraConstants.PROBABILITY_APPCODE;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.kernel.commands.selector.CommandFilterFactory;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.exam.restriction.ExamRestrictionModel;

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

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			// no restrictions -> no default needed
		}
	},
	/*MMS() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Deutschland IQB MMS Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "MMS Abitur";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setSubAppCodes(GRAPHING_APPCODE, GEOMETRY_APPCODE, G3D_APPCODE);
			model.setCommandFilter(CommandFilterFactory.createMmsFilter());
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(CAS_APPCODE);
		}
	},*/
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
			model.setRestrictedSubAppCodes(G3D_APPCODE);
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(GRAPHING_APPCODE);
		}
	},
	BAYERN_CAS() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Schulversuch CAS in Pr\u00FCfungen";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Schulversuch CAS";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setRestrictedSubAppCodes(GRAPHING_APPCODE, GEOMETRY_APPCODE, G3D_APPCODE,
					PROBABILITY_APPCODE);
			model.setCommandFilter(CommandFilterFactory.createBayernCasFilter());
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(CAS_APPCODE);
		}
	},
	VLAANDEREN() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Vlaanderen";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Vlaanderen";
		}

		@Override
		public void applyRestrictions(ExamRestrictionModel model) {
			model.setRestrictedSubAppCodes(CAS_APPCODE);
			model.setCommandFilter(CommandFilterFactory.createVlaanderenFilter());
		}

		@Override
		public void setDefaultSubAppCode(ExamRestrictionModel model) {
			model.setDefaultAppCode(GRAPHING_APPCODE);
		}
	};

	public static final String CHOOSE = "choose";

	/**
	 * Case-insensitive version of valueOf
	 * @param shortName exam name
	 * @return exam region
	 */
	public static ExamRegion byName(String shortName) {
		for (ExamRegion region: values()) {
			if (region.name().equalsIgnoreCase(shortName)) {
				return region;
			}
		}
		return null;
	}

	/**
	 * @param appCode app code for API (suite/graphing/classic/...)
	 * @return list of supported mode IDs
	 */
	public static String getSupportedModes(String appCode) {
		return Stream.concat(Stream.of(appCode, CHOOSE), Arrays.stream(ExamRegion.values())
					.filter(r -> r != ExamRegion.GENERIC)
					.map(r -> r.name().toLowerCase(Locale.ROOT)))
					.collect(Collectors.joining(", "));
	}

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	@Deprecated
	public abstract void applyRestrictions(ExamRestrictionModel model);

	@Deprecated
	public abstract void setDefaultSubAppCode(ExamRestrictionModel model);
}
