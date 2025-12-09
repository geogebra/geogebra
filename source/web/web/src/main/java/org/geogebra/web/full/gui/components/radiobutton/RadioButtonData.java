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

package org.geogebra.web.full.gui.components.radiobutton;

public class RadioButtonData<T> {
	private String label;
	private boolean disabled;
	private T value;

	/**
	 * radio button data
	 * @param label - label
	 * @param disabled - whether is disabled
	 */
	public RadioButtonData(String label, boolean disabled,
			T value) {
		this.label = label;
		this.disabled = disabled;
		this.value = value;
	}

	/**
	 * not disabled radio button data
	 * @param label - label
	 * @param value - value on click
	 */
	public RadioButtonData(String label, T value) {
		this(label, false, value);
	}

	public String getLabel() {
		return label;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public T getValue() {
		return value;
	}
}
