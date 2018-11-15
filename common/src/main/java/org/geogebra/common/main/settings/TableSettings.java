package org.geogebra.common.main.settings;

/**
 * Settings for function table
 * 
 * @author Zbynek
 */
public class TableSettings extends AbstractSettings {

	private double valuesMin = -2.0;
	private double valuesMax = 2.0;
	private double valuesStep = 1.0;

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
