package geogebra.common.main.settings;

//import geogebra.gui.view.probcalculator.ProbabilityManager;

import java.util.LinkedList;

/**
 * Settings for the probability calculator view.
 */
public class ProbabilityCalculatorSettings extends AbstractSettings {
	
	public enum DIST { NORMAL, STUDENT, CHISQUARE, F, CAUCHY, EXPONENTIAL, GAMMA, WEIBULL, LOGISTIC, LOGNORMAL, ERLANG, BINOMIAL, PASCAL, HYPERGEOMETRIC, POISSON };

	/*
	// continuous distribution identifiers
	public static final int DIST_NORMAL = 0;
	public static final int DIST_STUDENT = 1;
	public static final int DIST_CHISQUARE = 2;
	public static final int DIST_F = 3;
	public static final int DIST_CAUCHY = 4;
	public static final int DIST_EXPONENTIAL = 5;
	public static final int DIST_GAMMA = 6;
	public static final int DIST_WEIBULL = 7;
	public static final int DIST_LOGISTIC = 8;
	public static final int DIST_LOGNORMAL = 9;
	public static final int DIST_ERLANG = 10;
	//public static final int DIST_UNIFORM = 11;
	//public static final int DIST_TRIANGULAR = 12;

	// discrete distribution identifiers
	public static final int DIST_BINOMIAL = 11;
	public static final int DIST_PASCAL = 12;
	public static final int DIST_HYPERGEOMETRIC = 13;
	public static final int DIST_POISSON = 14;*/

	public static final int distCount = DIST.values().length;

	private double[] parameters = {0.0d, 1.0d};
	private DIST distributionType = DIST.NORMAL;
	private boolean isCumulative = false;
	
	public ProbabilityCalculatorSettings(LinkedList<SettingListener> listeners) {
		super(listeners);
	}

	public ProbabilityCalculatorSettings() {
		super();
	}
	
	
	
	/**
	 * Sets the parameter array
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
	 * Sets the  distribution type
	 */
	public void setDistributionType(DIST distributionType) {
		if(distributionType == null){
			distributionType = DIST.NORMAL; // default guard
		}
		this.distributionType = distributionType;
			settingChanged();
	}
	
	/** 
	 * @return distribution type
	 */
	public DIST getDistributionType() {
		return distributionType;
	}
	
	
	/**
	 * Sets the  cumulative flag
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
	
	
}
