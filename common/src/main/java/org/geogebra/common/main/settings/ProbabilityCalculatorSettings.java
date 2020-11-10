package org.geogebra.common.main.settings;

//import geogebra.gui.view.probcalculator.ProbabilityManager;

import java.util.LinkedList;

import org.geogebra.common.gui.view.probcalculator.StatisticsCollection;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Settings for the probability calculator view.
 */
public class ProbabilityCalculatorSettings extends AbstractSettings {
	/** distributions */
	public enum Dist {
		/** normal */
		NORMAL,
		/** student */
		STUDENT,
		/** chi squares */
		CHISQUARE,
		/** f distribution */
		F,
		/** Cauchy */
		CAUCHY,
		/** exponential */
		EXPONENTIAL,
		/** gamma dist */
		GAMMA,
		/** weibull */
		WEIBULL,
		/** logistic */
		LOGISTIC,
		/** log-normal */
		LOGNORMAL,
		/** erlang */
		ERLANG,
		/** binomial */
		BINOMIAL,
		/** pascal */
		PASCAL,
		/** hypergeometric */
		HYPERGEOMETRIC,
		/** poisson */
		POISSON
	}

	/*
	 * // continuous distribution identifiers public static final int
	 * DIST_NORMAL = 0; public static final int DIST_STUDENT = 1; public static
	 * final int DIST_CHISQUARE = 2; public static final int DIST_F = 3; public
	 * static final int DIST_CAUCHY = 4; public static final int
	 * DIST_EXPONENTIAL = 5; public static final int DIST_GAMMA = 6; public
	 * static final int DIST_WEIBULL = 7; public static final int DIST_LOGISTIC
	 * = 8; public static final int DIST_LOGNORMAL = 9; public static final int
	 * DIST_ERLANG = 10; //public static final int DIST_UNIFORM = 11; //public
	 * static final int DIST_TRIANGULAR = 12;
	 * 
	 * // discrete distribution identifiers public static final int
	 * DIST_BINOMIAL = 11; public static final int DIST_PASCAL = 12; public
	 * static final int DIST_HYPERGEOMETRIC = 13; public static final int
	 * DIST_POISSON = 14;
	 */
	/** number of distributions */
	public static final int distCount = Dist.values().length;

	private GeoNumeric[] parameters = { };
	private Dist distributionType = Dist.NORMAL;
	private boolean isCumulative = false;
	private boolean intervalSet = false;

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

	/**
	 * @param probMode
	 *            mode (left / right / interval)
	 */
	public void setProbMode(int probMode) {
		intervalSet = true;
		this.probMode = probMode;
		settingChanged();
	}

	/**
	 * @param low
	 *            lower bound
	 */
	public void setLow(GeoNumberValue low) {
		intervalSet = true;
		this.low = low;
		settingChanged();
	}

	/**
	 * @param high
	 *            upper bound
	 */
	public void setHigh(GeoNumberValue high) {
		intervalSet = true;
		this.high = high;
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
}
