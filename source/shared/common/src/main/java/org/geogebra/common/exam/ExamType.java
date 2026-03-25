/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.exam;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.exam.restrictions.BayernCasExamRestrictions;
import org.geogebra.common.exam.restrictions.CvteExamRestrictions;
import org.geogebra.common.exam.restrictions.GenericExamRestrictions;
import org.geogebra.common.exam.restrictions.IBExamRestrictions;
import org.geogebra.common.exam.restrictions.MmsExamRestrictions;
import org.geogebra.common.exam.restrictions.NiedersachsenExamRestrictions;
import org.geogebra.common.exam.restrictions.RealschuleExamRestrictions;
import org.geogebra.common.exam.restrictions.VlaanderenExamRestrictions;
import org.geogebra.common.exam.restrictions.WtrExamRestrictions;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.restrictions.Restrictions;
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
	 * @return The {@link Restrictions} for this exam type.
	 */
	public Restrictions createRestrictions() {
		switch (this) {
		case BAYERN_CAS:
			return new BayernCasExamRestrictions();
		case CVTE:
			return new CvteExamRestrictions();
		case IB:
			return new IBExamRestrictions();
		case NIEDERSACHSEN:
			return new NiedersachsenExamRestrictions();
		case BAYERN_GR:
			return new RealschuleExamRestrictions();
		case VLAANDEREN:
			return new VlaanderenExamRestrictions();
		case MMS:
			return new MmsExamRestrictions();
		case WTR:
			return new WtrExamRestrictions();
		default:
			return new GenericExamRestrictions();
		}
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
		if (this == ExamType.IB) {
			return PreviewFeature.isAvailable(PreviewFeature.IB_EXAM);
		}
		return true;
	}
}
