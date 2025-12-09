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

import javax.annotation.CheckForNull;

import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Localization;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.AbstractGeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.LineStylePropertyDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;

/**
 * Line style
 */
public class LineStyleProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private static final PropertyResource[] icons =
			EuclidianStyleConstants.lineStyleIcons.toArray(new PropertyResource[0]);
	private final AbstractGeoElementDelegate delegate;
	private final boolean hidden;

	/***/
	public LineStyleProperty(Localization localization, GeoElement element, boolean hidden)
			throws NotApplicablePropertyException {
		super(localization, "LineStyle");
		delegate = new LineStylePropertyDelegate(element);
		this.hidden = hidden;
		if (hidden && !element.getKernel().getApplication()
				.isEuclidianView3Dinited()) {
			throw new NotApplicablePropertyException(element);
		}
		setValues(EuclidianStyleConstants.lineStyleList);
	}

	@Override
	public PropertyResource[] getValueIcons() {
		return icons;
	}

	@Override
	public @CheckForNull String[] getToolTipLabels() {
		return null;
	}

	@Override
	protected void doSetValue(Integer value) {
		GeoElement element = delegate.getElement();
		if (hidden) {
			element.setLineTypeHidden(value);
		} else {
			element.setLineType(value);
		}
		element.updateVisualStyleRepaint(GProperty.LINE_STYLE);
	}

	@Override
	public Integer getValue() {
		return hidden ? delegate.getElement().getLineTypeHidden()
				: delegate.getElement().getLineType();
	}

	@Override
	public boolean isEnabled() {
		return delegate.getElement().isEuclidianVisible();
	}
}
