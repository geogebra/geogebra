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

import java.util.List;

import javax.annotation.CheckForNull;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.HasTextFormatter;
import org.geogebra.common.kernel.geos.properties.HorizontalAlignment;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TextFormatterDelegate;

public class HorizontalAlignmentProperty extends AbstractEnumeratedProperty<HorizontalAlignment>
		implements IconsEnumeratedProperty<HorizontalAlignment> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_ALIGNMENT_LEFT, PropertyResource.ICON_ALIGNMENT_CENTER,
			PropertyResource.ICON_ALIGNMENT_RIGHT
	};

	private final GeoElementDelegate delegate;

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the construction element
	 */
	public HorizontalAlignmentProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.HorizontalAlign");
		delegate = new TextFormatterDelegate(element);
		setValues(List.of(HorizontalAlignment.LEFT,
				HorizontalAlignment.CENTER,
				HorizontalAlignment.RIGHT));
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
	protected void doSetValue(HorizontalAlignment value) {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		HasTextFormat formatter = element.getFormatter();
		if (getLocalization() != null && formatter != null
				&& !value.equals(formatter.getHorizontalAlignment())) {
			formatter.setHorizontalAlignment(value);
		}
		((GeoElement) element).updateVisualStyle(GProperty.COMBINED);
	}

	@Override
	public HorizontalAlignment getValue() {
		HasTextFormatter element = (HasTextFormatter) delegate.getElement();
		HasTextFormat formatter = element.getFormatter();
		if (formatter != null) {
			return formatter.getHorizontalAlignment();
		}
		return HorizontalAlignment.LEFT;
	}
}
