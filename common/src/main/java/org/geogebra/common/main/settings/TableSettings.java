package org.geogebra.common.main.settings;

public class TableSettings extends AbstractSettings {

	private double valuesMin = -2.0;
	private double valuesMax = 2.0;
	private double valuesStep = 1.0;

	public double getValuesMin() {
		return valuesMin;
	}

	public void setValuesMin(double valuesMin) {
		this.valuesMin = valuesMin;
		settingChanged();
	}

	public double getValuesMax() {
		return valuesMax;
	}

	public void setValuesMax(double valuesMax) {
		this.valuesMax = valuesMax;
		settingChanged();
	}

	public double getValuesStep() {
		return valuesStep;
	}

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
