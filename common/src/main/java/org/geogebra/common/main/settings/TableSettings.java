package org.geogebra.common.main.settings;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.util.StringUtil;

/**
 * Settings for function table
 *
 * @author Zbynek
 */
public class TableSettings extends AbstractSettings {

	/** The default value for valuesMin */
	public static final double DEFAULT_MIN = -2;

	/** The default value for valuesMax */
	public static final double DEFAULT_MAX = 2;

	/** The default value for valuesStep */
	public static final double DEFAULT_STEP = 1;

	private double valuesMin = 0;
	private double valuesMax = 0;
	private double valuesStep = 0;

	private GeoList valueList;

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

	public GeoList getValueList() {
		return valueList;
	}

	public void setValueList(GeoList valueList) {
		this.valueList = valueList;
	}

	/**
	 * Sets the valueList and notifies the listeners.
	 * @param valueList x values list
	 */
	public void updateValueList(GeoList valueList) {
		this.valueList = valueList;
		settingChanged();
	}

	/**
	 * Serialize the settings.
	 *
	 * @param sb
	 *            XML builder
	 */
	public void getXML(StringBuilder sb) {
		sb.append("<tableview");
		if (valueList != null && valueList.isLabelSet()) {
			sb.append(" xValues=\"");
			sb.append(StringUtil.encodeXML(valueList.getLabel(StringTemplate.xmlTemplate)));
			sb.append("\"");
		} else {
			sb.append(" min=\"");
			sb.append(valuesMin);
			sb.append("\" max=\"");
			sb.append(valuesMax);
			sb.append("\" step=\"");
			sb.append(valuesStep);
			sb.append("\"");
		}
		sb.append("/>\n");
	}

	public void resetMinMaxStep() {
		valuesStep = valuesMax = valuesMin = 0;
	}
}
