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
		low = null;
		high = null;
		settingChanged();
	}
}
