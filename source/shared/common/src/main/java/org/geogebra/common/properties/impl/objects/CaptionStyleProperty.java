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

package org.geogebra.common.properties.impl.objects;

import static java.util.Map.entry;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractNamedEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.CaptionStyleDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Caption style
 */
public class CaptionStyleProperty extends AbstractNamedEnumeratedProperty<Integer> {

	private static final List<Integer> labelModes = Arrays.asList(
			GeoElementND.LABEL_DEFAULT,
			GeoElementND.LABEL_NAME,
			GeoElementND.LABEL_NAME_VALUE,
			GeoElementND.LABEL_VALUE,
			GeoElementND.LABEL_CAPTION);

	private final AbstractGeoElementDelegate delegate;

	/***/
	public CaptionStyleProperty(Localization localization, GeoElement geoElement)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.Caption");
		delegate = new CaptionStyleDelegate(geoElement);
		setNamedValues(List.of(
				entry(GeoElementND.LABEL_HIDDEN, "Hidden"),
				entry(GeoElementND.LABEL_NAME, "Name"),
				entry(GeoElementND.LABEL_NAME_VALUE, "NameAndValue"),
				entry(GeoElementND.LABEL_VALUE, "Value"),
				entry(GeoElementND.LABEL_CAPTION, "Caption"),
				entry(GeoElementND.LABEL_DEFAULT, "CaptionAndValue")
		));
	}

	@Override
	public Integer getValue() {
		GeoElement element = delegate.getElement();
		if (!element.isLabelVisible()) {
			return GeoElementND.LABEL_HIDDEN;
		}
		int labelMode = element.getLabelMode();
		int index = labelModes.indexOf(labelMode);
		if (index >= 0) {
			return labelMode;
		} else {
			return labelModes.get(1);
		}
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (value != GeoElementND.LABEL_HIDDEN) {
			element.setLabelMode(value);
		}
		element.setLabelVisible(value != GeoElementND.LABEL_HIDDEN);
		element.updateRepaint();
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
