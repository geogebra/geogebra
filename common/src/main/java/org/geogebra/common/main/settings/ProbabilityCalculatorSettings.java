package org.geogebra.common.main.settings;

//import geogebra.gui.view.probcalculator.ProbabilityManager;

import java.util.LinkedList;

/**
 * Settings for the probability calculator view.
 */
public class ProbabilityCalculatorSettings extends AbstractSettings {
	/** distributions */
	public enum DIST {
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
	public static final int distCount = DIST.values().length;

	private double[] parameters = { 0.0d, 1.0d };
	private DIST distributionType = DIST.NORMAL;
	private boolean isCumulative = false;
	private boolean intervalSet = false;

	private int probMode;

	private double low;

	private double high;

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
	public void setParameters(double[] parameters) {
		this.parameters = parameters;
		settingChanged();
	}

	/**
	 * @return parameter array
	 */
	public double[] getParameters() {
		return parameters;
	}

	/**
	 * Sets the distribution type
	 * 
	 * @param distributionType
	 *            dist type
	 */
	public void setDistributionType(DIST distributionType) {
		if (distributionType == null) {
			this.distributionType = DIST.NORMAL; // default guard
		} else {
			this.distributionType = distributionType;
		}
		settingChanged();
	}

	/**
	 * @return distribution type
	 */
	public DIST getDistributionType() {
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
	public void setLow(double low) {
		intervalSet = true;
		this.low = low;
		settingChanged();
	}

	/**
	 * @param high
	 *            upper bound
	 */
	public void setHigh(double high) {
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
	public double getLow() {
		return this.low;
	}

	/**
	 * @return upper bound
	 */
	public double getHigh() {
		return this.high;
	}
}
