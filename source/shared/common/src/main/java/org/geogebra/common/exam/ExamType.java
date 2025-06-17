package org.geogebra.common.exam;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.restrictions.cvte.CvteAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.mms.MmsAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.realschule.RealschuleAlgebraOutputFilter;
import org.geogebra.common.exam.restrictions.wtr.WtrAlgebraOutputFilter;
import org.geogebra.common.gui.view.algebra.filter.AlgebraOutputFilter;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.util.ToStringConverter;

/**
 * Exam types.
 */
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
				@CheckForNull AlgebraOutputFilter wrappedFilter) {
			return new CvteAlgebraOutputFilter(wrappedFilter);
		}
	},

	BAYERN_GR() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Bayern Grafikrechner";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "Bayern GR";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@CheckForNull AlgebraOutputFilter wrappedFilter) {
			return new RealschuleAlgebraOutputFilter(wrappedFilter);
		}
	},

	MMS() {
		@Override
		public String getDisplayName(Localization loc, AppConfig config) {
			return "Deutschland MMS Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "MMS Abitur";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@CheckForNull AlgebraOutputFilter wrappedFilter) {
			return new MmsAlgebraOutputFilter(wrappedFilter);
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
			return "Deutschland WTR Abitur";
		}

		@Override
		public String getShortDisplayName(Localization loc, AppConfig config) {
			return "WTR";
		}

		@Override
		public AlgebraOutputFilter wrapAlgebraOutputFilter(
				@CheckForNull AlgebraOutputFilter wrappedFilter) {
			return new WtrAlgebraOutputFilter(wrappedFilter);
		}
	};

	public static final String CHOOSE = "choose";

	/**
	 * Case-insensitive version of valueOf, returns null if name is invalid or null
	 * @param shortName exam name
	 * @return exam type or null
	 */
	public static ExamType byName(@CheckForNull String shortName) {
		for (ExamType region: values()) {
			if (region.name().equalsIgnoreCase(shortName)) {
				return region;
			}
		}
		return null;
	}

	/**
	 * Get name for the start dialog.
	 * @param loc localization
	 * @param config application config
	 * @return full display name
	 */
	public abstract String getDisplayName(Localization loc, AppConfig config);

	/**
	 * Get name for a status bar.
	 * @param loc localization
	 * @param config application config
	 * @return short display name
	 */
	public abstract String getShortDisplayName(Localization loc, AppConfig config);

	/**
	 * @param wrappedFilter The currently used {@link AlgebraOutputFilter}
	 * @return The output filter for this exam type. By default, returns the currently used filter.
	 */
	public AlgebraOutputFilter wrapAlgebraOutputFilter(
			@CheckForNull AlgebraOutputFilter wrappedFilter) {
		return wrappedFilter;
	}

	/**
	 * @param wrappedConverter The currently used value converter
	 * @return The value converter for this exam type. By default, returns the currently used
	 * value converter.
	 */
	public ToStringConverter wrapValueConverter(
			@Nonnull ToStringConverter wrappedConverter) {
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
		case IB:
			return PreviewFeature.isAvailable(PreviewFeature.IB_EXAM);
		case MMS:
			return PreviewFeature.isAvailable(PreviewFeature.MMS_EXAM);
		default:
			return true;
		}
	}
}
