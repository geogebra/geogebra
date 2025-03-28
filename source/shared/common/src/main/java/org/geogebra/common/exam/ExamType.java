package org.geogebra.common.exam;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.restrictions.cvte.CvteAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.cvte.CvteValueConverter;
import org.geogebra.common.exam.restrictions.mms.MmsAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.mms.MmsValueConverter;
import org.geogebra.common.exam.restrictions.realschule.RealschuleAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.realschule.RealschuleValueConverter;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.ToStringConverter;

public enum ExamType {

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
	},

	CVTE() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "CvTE goedgekeurde examenstand";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "CvTE";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@Nullable AlgebraOutputFilter wrappedFilter) {
			return new CvteAlgebraOutputFilter(wrappedFilter);
		}

		@Override
		public ToStringConverter wrapValueConverter(
				@Nonnull ToStringConverter wrappedConverter) {
			return new CvteValueConverter(wrappedConverter);
		}
	},

	REALSCHULE() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Bayern Realschulrechner";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Realschule";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@Nullable AlgebraOutputFilter wrappedFilter) {
			return new RealschuleAlgebraOutputFilter(wrappedFilter);
		}

		@Override
		public ToStringConverter wrapValueConverter(
				@Nonnull ToStringConverter wrappedConverter) {
			return new RealschuleValueConverter(wrappedConverter);
		}
	},

	MMS() {

		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Deutschland IQB MMS Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "MMS Abitur";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@Nullable AlgebraOutputFilter wrappedFilter) {
			return new MmsAlgebraOutputFilter(wrappedFilter);
		}

		@Override
		public ToStringConverter wrapValueConverter(@Nonnull ToStringConverter wrappedConverter) {
			return new MmsValueConverter(wrappedConverter);
		}
	},

	IB() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "IB Exam";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "IB Exam";
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
	},

	WTR() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Deutschland IQB WTR Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "WTR";
		}
	};

	public static final String CHOOSE = "choose";

	/**
	 * Case-insensitive version of valueOf, returns null if name is invalid or null
	 * @param shortName exam name
	 * @return exam type or null
	 */
	public static ExamType byName(@Nullable String shortName) {
		for (ExamType region: values()) {
			if (region.name().equalsIgnoreCase(shortName)) {
				return region;
			}
		}
		return null;
	}

	public abstract String getDisplayName(Localization loc, AppConfig config);

	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	/**
	 * @param wrappedFilter The currently used {@link AlgebraOutputFilter}
	 * @return The output filter for this exam type. By default, returns the currently used filter.
	 */
	public AlgebraOutputFilter wrapAlgebraOutputFilter(
			@Nullable AlgebraOutputFilter wrappedFilter) {
		return wrappedFilter;
	}

	/**
	 * @param wrappedConverter The currently used value converter
	 * @return The value converter for this exam type. By default, returns the currently used
	 * value converter.
	 */
	public ToStringConverter wrapValueConverter(
			@Nullable ToStringConverter wrappedConverter) {
		return wrappedConverter;
	}

	/**
	 * List of exam types sorted by localized names (except GENERIC goes first)
	 * @param loc localization
	 * @param config app config
	 * @return available types
	 */
	public static List<ExamType> getAvailableValues(Localization loc, AppConfig config) {
		Comparator<ExamType> genericFirst = Comparator.comparing(type -> !GENERIC.equals(type));
		return Arrays.stream(values()).filter(ExamType::isAvailable)
				.sorted(genericFirst.thenComparing(type -> type.getDisplayName(loc, config)))
				.collect(Collectors.toList());
	}

	private boolean isAvailable() {
		switch (this) {
		case CVTE:
			return PreviewFeature.isAvailable(PreviewFeature.CVTE_EXAM);
		case IB:
			return PreviewFeature.isAvailable(PreviewFeature.IB_EXAM);
		case MMS:
			return PreviewFeature.isAvailable(PreviewFeature.MMS_EXAM);
		case REALSCHULE:
			return PreviewFeature.isAvailable(PreviewFeature.REALSCHULE_EXAM);
		case WTR:
			return PreviewFeature.isAvailable(PreviewFeature.WTR_EXAM);
		default:
			return true;
		}
	}
}
