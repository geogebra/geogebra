package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.main.Localization;

import com.himamis.retex.editor.share.util.Unicode;

public enum Stat {
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

	private final Commands cmd;
	private final String lhsPattern;

	Stat(Commands cmd) {
		this.cmd = cmd;
		lhsPattern = "%t";
	}

	Stat(Commands cmd, String pattern) {
		this.cmd = cmd;
		this.lhsPattern = pattern;
	}

	public String getCommandName() {
		return cmd.name();
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
