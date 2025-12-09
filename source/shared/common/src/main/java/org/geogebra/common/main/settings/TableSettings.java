/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.main.settings;

import org.geogebra.common.io.XMLStringBuilder;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;

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

	private String valueListCaption = "x";

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
	 * @return the caption for the (x) values column
	 */
	public String getValueListCaption() {
		return valueListCaption;
	}

	/**
	 * Sets the caption for the (x) values column
	 * @param valueListCaption the caption for the (x) values column
	 */
	public void setValueListCaption(String valueListCaption) {
		this.valueListCaption = valueListCaption;
	}

	/**
	 * Serialize the settings.
	 *
	 * @param sb
	 *            XML builder
	 */
	public void getXML(XMLStringBuilder sb) {
		sb.startTag("tableview", 0);
		if (valueList != null && valueList.isLabelSet()) {
			sb.attr("xValues", valueList.getLabel(StringTemplate.xmlTemplate));
			if (valueListCaption != null) {
				sb.attr("xCaption", valueListCaption);
			}
		} else {
			sb.attr("min", valuesMin);
			sb.attr("max", valuesMax);
			sb.attr("step", valuesStep);
		}
		sb.endTag();
	}

	/**
	 * Reset min, max and step.
	 */
	public void resetMinMaxStep() {
		valuesStep = valuesMax = valuesMin = 0;
	}
}
