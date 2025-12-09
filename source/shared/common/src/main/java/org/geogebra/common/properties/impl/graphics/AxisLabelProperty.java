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

package org.geogebra.common.properties.impl.graphics;

import static org.geogebra.common.gui.dialog.options.model.AxisModel.AXIS_X;
import static org.geogebra.common.gui.dialog.options.model.AxisModel.AXIS_Y;
import static org.geogebra.common.gui.dialog.options.model.AxisModel.AXIS_Z;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.PreviewFeature;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.properties.impl.AbstractValuedProperty;
import org.geogebra.common.properties.util.StringPropertyWithSuggestions;

/**
 * This property controls the label on an axis.
 */
public class AxisLabelProperty extends AbstractValuedProperty<String>
		implements StringPropertyWithSuggestions {

	private EuclidianSettings euclidianSettings;
	private int axis;

	/**
	 * Constructs an xAxis property.
	 * @param localization localization for the title
	 * @param euclidianSettings euclidian settings
	 * @param label the name of the axis
	 * @param axis the axis for label
	 */
	public AxisLabelProperty(Localization localization,
			EuclidianSettings euclidianSettings, String label, int axis) {
		super(localization, label);
		this.euclidianSettings = euclidianSettings;
		this.axis = axis;
	}

	@Override
	public List<String> getSuggestions() {
		ArrayList<String> labels = new ArrayList<>();
		labels.add("");
		String defaultLabel;
		switch (axis) {
		case AXIS_X:
			defaultLabel = "x";
			break;
		case AXIS_Y:
		default:
			defaultLabel = "y";
			break;
		case AXIS_Z:
			defaultLabel = "z";
			break;
		}
		labels.add(defaultLabel);
		GeoElement.addAddAllGreekLowerCaseNoPi(labels);
		return labels;
	}

	@Override
	public String getValue() {
		if (!isEnabled()) {
			return EuclidianSettings.getDefaultAxisLabel(axis);
		}
		String axisLabel = euclidianSettings.getAxesLabels()[axis];
		return axisLabel == null ? "" : axisLabel;
	}

	@Override
	public void doSetValue(String value) {
		euclidianSettings.setAxisLabel(axis, value);
	}

	@Override
	public @CheckForNull String validateValue(String value) {
		return null;
	}

	@Override
	public boolean isEnabled() {
		if (PreviewFeature.isAvailable(PreviewFeature.SETTINGS_VIEW)) {
			return true;
		}
		String[] labels = euclidianSettings.getAxesLabels();
		boolean enabled = false;
		for (int i = 0; i < euclidianSettings.getDimension(); i++) {
			enabled |= labels[i] != null;
		}
		return enabled;
	}
}
