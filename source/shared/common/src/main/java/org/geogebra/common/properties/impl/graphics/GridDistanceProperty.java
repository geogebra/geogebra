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

import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.properties.NumericPropertyWithSuggestions;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.editor.share.util.Unicode;

public class GridDistanceProperty extends NumericPropertyWithSuggestions
		implements SettingsDependentProperty {
	private final EuclidianViewInterfaceCommon euclidianView;
	private final int axis;

	/**
	 * Constructs a grid distance property.
	 * @param processor algebra processor
	 * @param localization localization for the title
	 * @param euclidianView euclidian view
	 * @param label label of the axis
	 * @param axis the axis for the numbering distance will be set
	 */
	public GridDistanceProperty(AlgebraProcessor processor, Localization localization,
			EuclidianViewInterfaceCommon euclidianView, String label, int axis) {
		super(processor, localization, label);
		this.euclidianView = euclidianView;
		this.axis = axis;
	}

	@Override
	public List<String> getSuggestions() {
		return List.of("1", Unicode.PI_STRING, Unicode.PI_HALF_STRING);
	}

	@Override
	protected void setNumberValue(GeoNumberValue value) {
		if (!Double.isFinite(value.getDouble())) {
			return;
		}
		double[] ticks = euclidianView.getGridDistances();
		ticks[axis] = value.getDouble();
		euclidianView.setGridDistances(ticks);
		euclidianView.updateBackground();
	}

	@Override
	protected NumberValue getNumberValue() {
		return new MyDouble(euclidianView.getKernel(), euclidianView.getGridDistances()[axis]);
	}

	@Override
	public String getValue() {
		double distance = euclidianView.getGridDistances()[axis];
		if (DoubleUtil.isEqual(distance, Math.PI)) {
			return Unicode.PI_STRING;
		}  else if (DoubleUtil.isEqual(distance, Kernel.PI_HALF)) {
			return Unicode.PI_HALF_STRING;
		} else {
			return super.getValue();
		}
	}

	@Override
	public AbstractSettings getSettings() {
		return euclidianView.getSettings();
	}

	@Override
	public boolean isAvailable() {
		boolean polar = euclidianView.getSettings().getGridType() == EuclidianView.GRID_POLAR;
		return "r".equals(getRawName()) == polar;
	}

	@Override
	public boolean isEnabled() {
		return !euclidianView.getSettings().getAutomaticGridDistance();
	}
}
