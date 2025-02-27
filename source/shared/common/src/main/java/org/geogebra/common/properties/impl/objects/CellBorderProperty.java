package org.geogebra.common.properties.impl.objects;

import java.util.List;

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
	 * @param element the name of the property
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
		return formatter.getBorderStyle();
	}
}
