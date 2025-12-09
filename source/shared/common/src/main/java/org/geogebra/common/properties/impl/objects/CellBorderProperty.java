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

import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.kernel.geos.properties.BorderType;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.IconsEnumeratedProperty;
import org.geogebra.common.properties.PropertyResource;
import org.geogebra.common.properties.impl.AbstractEnumeratedProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TablePropertyDelegate;

public class CellBorderProperty extends AbstractEnumeratedProperty<BorderType>
		implements IconsEnumeratedProperty<BorderType> {

	private static final PropertyResource[] icons = {
			PropertyResource.ICON_CELL_BORDER_ALL, PropertyResource.ICON_CELL_BORDER_INNER,
			PropertyResource.ICON_CELL_BORDER_OUTER, PropertyResource.ICON_CELL_BORDER_NONE};

	private final GeoElementDelegate delegate;

	/**
	 * Constructs an AbstractEnumeratedProperty.
	 * @param localization the localization used
	 * @param element the construction element
	 */
	public CellBorderProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "stylebar.Borders");
		delegate = new TablePropertyDelegate(element);
		setValues(List.of(BorderType.ALL, BorderType.INNER, BorderType.OUTER, BorderType.NONE));
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
	protected void doSetValue(BorderType value) {
		InlineTableController formatter =
				(InlineTableController) ((GeoInlineTable) delegate.getElement()).getFormatter();
		formatter.setBorderStyle(value);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public BorderType getValue() {
		InlineTableController formatter =
				(InlineTableController) ((GeoInlineTable) delegate.getElement()).getFormatter();
		return formatter != null ? formatter.getBorderStyle() : BorderType.ALL;
	}
}
