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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.Localization;
import org.geogebra.editor.share.util.Unicode;

/**
 * Statistics to compute from one or two sets of data.
 */
public enum Statistic {
	NULL(null), LENGTH(Commands.Length), MEAN(Commands.mean, "\\overline{%v}"),
	SD(Commands.SD, "%t%v"), SAMPLE_SD(Commands.SampleSD, "%t%v"),
	SUM(Commands.Sum, "\\Sigma %v"),
	SIGMAXX(Commands.SigmaXX, "\\Sigma %v" + Unicode.SUPERSCRIPT_2),
	MIN(Commands.Min, "%t(%v)"), Q1(Commands.Quartile1), MEDIAN(Commands.Median),
	Q3(Commands.Q3), MAX(Commands.Max, "%t(%v)"), MEANX(Commands.MeanX),
	MEANY(Commands.MeanY), SX(Commands.SampleSDX), SY(Commands.SampleSDY), PMCC(Commands.PMCC),
	SPEARMAN(Commands.Spearman), SXX(Commands.SXX), SYY(Commands.SYY), SXY(Commands.SXY),
	RSQUARE(Commands.RSquare), SSE(Commands.SumSquaredErrors),
	SIGMAXY(Commands.SigmaXY, "\\Sigma %v"),
	COVARIANCE(Commands.Covariance, "cov");

	private final Commands command;
	private final String lhsPattern;

	Statistic(Commands command) {
		this(command, "%t");
	}

	Statistic(Commands command, String lhsPattern) {
		this.command = command;
		this.lhsPattern = lhsPattern;
	}

	public String getCommandName() {
		return command.name();
	}

	/**
	 * @return localization key used by {@link Localization#getMenu(String)}.
	 */
	public String getMenuLocalizationKey() {
		return "Stats." + getCommandName();
	}

	/**
	 * Used by classic data analysis
	 * @return translation key
	 */
	public String getTranslationKey() {
		switch (this) {
		case LENGTH:
			return "Length.short";
		case MEAN:
			return "Mean";
		case SD:
			return "StandardDeviation.short";
		case SAMPLE_SD:
			return "SampleStandardDeviation.short";
		case SUM:
			return "Sum";
		case SIGMAXX:
			return "Sum2";
		case MIN:
			return "Minimum.short";
		case Q1:
			return "LowerQuartile.short";
		case MEDIAN:
			return "Median";
		case Q3:
			return "UpperQuartile.short";
		case MAX:
			return "Maximum.short";
		case MEANX:
			return "MeanX";
		case MEANY:
			return "MeanY";
		case SX:
			return "Sx";
		case SY:
			return "Sy";
		case PMCC:
			return "CorrelationCoefficient.short";
		case SPEARMAN:
			return "Spearman.short";
		case SXX:
			return "Sxx";
		case SYY:
			return "Syy";
		case SXY:
			return "Sxy";
		case RSQUARE:
			return "RSquare.Short";
		case SSE:
			return "SumSquaredErrors.short";
		default:
			return null;
		}
	}

	/**
	 * @param loc localization
	 * @return localized name
	 */
	public String getLocalizedName(Localization loc) {
		return loc.getCommand(getCommandName());
	}

	/**
	 * @param loc localization
	 * @param variableName variable name
	 * @return localized LHS for value row in stats dialog
	 */
	public String getLHS(Localization loc, String variableName) {
		return lhsPattern.replace("%v", variableName)
					.replace("%t", loc.getMenu(getTranslationKey()));
	}
}
