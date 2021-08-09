package org.geogebra.common.main.settings;

/**
 * Settings for function table
 * 
 * @author Zbynek
 */
public class TableSettings extends AbstractSettings {

	/** The default value for valuesMin */
	public static final double DEFAULT_MIN = 0;

	/** The default value for valuesMax */
	public static final double DEFAULT_MAX = 0;

	/** The default value for valuesStep */
	public static final double DEFAULT_STEP = 0;

	private double valuesMin = DEFAULT_MIN;
	private double valuesMax = DEFAULT_MAX;
	private double valuesStep = DEFAULT_STEP;

	/**
	 * @return min x-value for function table
	 */
	public double getValuesMin() {
		return valuesMin;
	}

	/**
	 * @param valuesMin
	 *            min x-value for function table
	 */
	public void setValuesMin(double valuesMin) {
		this.valuesMin = valuesMin;
		settingChanged();
	}

	/**
	 * @return max x-value for function table
	 */
	public double getValuesMax() {
		return valuesMax;
	}

	/**
	 * @param valuesMax
	 *            max x-value for function table
	 */
	public void setValuesMax(double valuesMax) {
		this.valuesMax = valuesMax;
		settingChanged();
	}

	/**
	 * @return increment of x-value for function table
	 */
	public double getValuesStep() {
		return valuesStep;
	}

	/**
	 * @param valuesStep
	 *            increment of x-value for function table
	 */
	public void setValuesStep(double valuesStep) {
		this.valuesStep = valuesStep;
		settingChanged();
	}

	/**
	 * Serialize the settings.
	 * 
	 * @param sb
	 *            XML builder
	 */
	public void getXML(StringBuilder sb) {
		sb.append("<tableview min=\"");
		sb.append(valuesMin);
		sb.append("\" max=\"");
		sb.append(valuesMax);
		sb.append("\" step=\"");
		sb.append(valuesStep);
		sb.append("\"/>\n");
	}

}
