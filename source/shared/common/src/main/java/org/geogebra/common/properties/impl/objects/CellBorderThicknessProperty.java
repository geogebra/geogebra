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

import static org.geogebra.common.properties.PropertyResource.ICON_CELL_BORDER_THICK;
import static org.geogebra.common.properties.PropertyResource.ICON_CELL_BORDER_THIN;

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

public class CellBorderThicknessProperty extends AbstractEnumeratedProperty<Integer>
		implements IconsEnumeratedProperty<Integer> {
	private static final PropertyResource[] icons = {
			ICON_CELL_BORDER_THIN, ICON_CELL_BORDER_THICK,
	};
	private final GeoElementDelegate delegate;

	/**
	 * Creates property for table border thickness.
	 * @param localization localization
	 * @param element geo
	 * @throws NotApplicablePropertyException exception if not applicable
	 */
	public CellBorderThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Thickness");
		delegate = new TablePropertyDelegate(element);
		setValues(List.of(1, 3));
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
		InlineTableController formatter = getFormatter();
		formatter.setBorderThickness(value);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		InlineTableController formatter = getFormatter();
		return formatter != null ? formatter.getBorderThickness() : 1;
	}

	private InlineTableController getFormatter() {
		GeoInlineTable table = (GeoInlineTable) delegate.getElement();
		return (InlineTableController) table.getFormatter();
	}

	@Override
	public boolean isEnabled() {
		InlineTableController formatter = getFormatter();
		return formatter != null && formatter.getBorderStyle() != BorderType.NONE;
	}
}
