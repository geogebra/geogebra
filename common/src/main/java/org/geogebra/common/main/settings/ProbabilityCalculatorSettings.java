package org.geogebra.common.main.settings;

//import geogebra.gui.view.probcalculator.ProbabilityManager;

import java.util.Arrays;
import java.util.LinkedList;

import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorView;
import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Settings for the probability calculator view.
 */
public class ProbabilityCalculatorSettings extends AbstractSettings {
	/** distributions */
	public enum Dist {
		/** normal */
		NORMAL(Commands.Normal, Commands.InverseNormal, "Distribution.Normal"),
		/** student */
		STUDENT(Commands.TDistribution, Commands.InverseTDistribution, "Distribution.StudentT"),
		/** chi squares */
		CHISQUARE(Commands.ChiSquared, Commands.InverseChiSquared, "Distribution.ChiSquare"),
		/** f distribution */
		F(Commands.FDistribution, Commands.InverseFDistribution, "Distribution.F"),
		/** Cauchy */
		CAUCHY(Commands.Cauchy, Commands.InverseCauchy, "Distribution.Cauchy"),
		/** exponential */
		EXPONENTIAL(Commands.Exponential, Commands.InverseExponential, "Distribution.Exponential"),
		/** beta dist */
		BETA(Commands.BetaDist, Commands.InverseBeta, "Distribution.Beta"),
		/** gamma dist */
		GAMMA(Commands.Gamma, Commands.InverseGamma, "Distribution.Gamma"),
		/** weibull */
		WEIBULL(Commands.Weibull, Commands.InverseWeibull, "Distribution.Weibull"),
		/** logistic */
		LOGISTIC(Commands.Logistic, Commands.InverseLogistic, "Distribution.Logistic"),
		/** log-normal */
		LOGNORMAL(Commands.LogNormal, Commands.InverseLogNormal, "Distribution.Lognormal"),
		/** binomial */
		BINOMIAL(Commands.BinomialDist, Commands.InverseBinomial, "Distribution.Binomial"),
		/** pascal */
		PASCAL(Commands.Pascal, Commands.InversePascal, "Distribution.Pascal"),
		/** hypergeometric */
		HYPERGEOMETRIC(Commands.HyperGeometric, Commands.InverseHyperGeometric, "Distribution.Hypergeometric"),
		/** poisson */
		POISSON(Commands.Poisson, Commands.InversePoisson, "Distribution.Poisson");

		public final Commands command;
		public final Commands inverse;
		public final String translationKey;

		Dist(Commands command, Commands inverse, String translationKey) {
			this.command = command;
			this.inverse = inverse;
			this.translationKey = translationKey;
		}

		/**
		 * @param command inverse distribution command
		 * @return corresponding distribution
		 */
		public static Dist forInverse(Commands command) {
			return Arrays.stream(values())
					.filter(s -> s.inverse == command).findFirst().orElse(null);
		}

		/**
		 * @param command distribution command
		 * @return corresponding distribution
		 */
		public static Dist forCommand(Commands command) {
			return Arrays.stream(values())
					.filter(s -> s.command == command).findFirst().orElse(null);
		}
	}

	/** number of distributions */
	public static final int distCount = Dist.values().length;

	private GeoNumeric[] parameters = { };
	private Dist distributionType = Dist.NORMAL;
	private boolean isCumulative = false;
	private boolean intervalSet = false;
	private boolean  isOverlayActive = false;

	private int probMode;

	private GeoNumberValue low;

	private GeoNumberValue high;

	private StatisticsCollection stats;

	/**
	 * @param listeners
	 *            listeners
	 */
	public ProbabilityCalculatorSettings(
			LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	/**
	 * Default constructor
	 */
	public ProbabilityCalculatorSettings() {
		super();
	}

	/**
	 * Sets the parameter array
	 * 
	 * @param parameters
	 *            distribution paramaeters
	 */
	public void setParameters(GeoNumeric[] parameters) {
		this.parameters = parameters;
		settingChanged();
	}

	/**
	 * @return parameter array
	 */
	public GeoNumeric[] getParameters() {
		return parameters;
	}

	/**
	 * Sets the distribution type
	 * 
	 * @param distributionType
	 *            dist type
	 */
	public void setDistributionType(Dist distributionType) {
		if (distributionType == null) {
			this.distributionType = Dist.NORMAL; // default guard
		} else {
			this.distributionType = distributionType;
		}
		settingChanged();
	}

	/**
	 * @return distribution type
	 */
	public Dist getDistributionType() {
		return distributionType;
	}

	/**
	 * Sets the cumulative flag
	 * 
	 * @param isCumulative
	 *            cumulative flag
	 */
	public void setCumulative(boolean isCumulative) {
		this.isCumulative = isCumulative;
		settingChanged();
	}

	/**
	 * @return cumulative flag
	 */
	public boolean isCumulative() {
		return isCumulative;
	}

	public boolean isOverlayActive() {
		return isOverlayActive;
	}

	/**
	 * @param isOverlayActive - whether overlay button was active
	 */
	public void setOverlayActive(boolean isOverlayActive) {
		this.isOverlayActive = isOverlayActive;
		settingChanged();
	}

	/**
	 * @param probMode
	 *            mode (left / right / interval)
	 * @param high interval upper bound
	 * @param low interval lower bound
	 */
	public void setProbInterval(int probMode, GeoNumberValue low, GeoNumberValue high) {
		intervalSet = true;
		this.probMode = probMode;
		this.high = high;
		this.low = low;
		settingChanged();
	}

	/**
	 * @return whether low or high are set
	 */
	public boolean isIntervalSet() {
		return intervalSet;
	}

	/**
	 * @return mode (left, right, interval)
	 */
	public int getProbMode() {
		return this.probMode;
	}

	/**
	 * @return lower bound
	 */
	public GeoNumberValue getLow() {
		return this.low;
	}

	/**
	 * @return upper bound
	 */
	public GeoNumberValue getHigh() {
		return this.high;
	}

	/**
	 * @param stats
	 *            stats collection
	 */
	public void setCollection(StatisticsCollection stats) {
		this.stats = stats;
	}

	/**
	 * @return stats collection
	 */
	public StatisticsCollection getCollection() {
		if (stats == null) {
			stats = new StatisticsCollection();
		}
		return stats;
	}

	/**
	 * Independent on resetDefaults
	 */
	public void reset() {
		intervalSet = false;
		isOverlayActive = false;
		low = null;
		high = null;
		distributionType = Dist.NORMAL;
		parameters = new GeoNumeric[0];
		probMode = ProbabilityCalculatorView.PROB_INTERVAL;
		settingChanged();
	}
}
