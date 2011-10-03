package geogebra.main.settings;

import geogebra.gui.view.probcalculator.ProbabilityManager;

import java.util.LinkedList;

/**
 * Settings for the probability calculator view.
 */
public class ProbabilityCalculatorSettings extends AbstractSettings {

	private double[] parameters = {0.0d, 1.0d};
	private int distributionType = ProbabilityManager.DIST_NORMAL;
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
	public void setDistributionType(int distributionType) {
		this.distributionType = distributionType;
			settingChanged();
	}
	
	/** 
	 * @return distribution type
	 */
	public int getDistributionType() {
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
