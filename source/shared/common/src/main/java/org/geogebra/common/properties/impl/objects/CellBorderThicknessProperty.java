package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.euclidian.inline.InlineTableController;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInlineTable;
import org.geogebra.common.main.Localization;
import org.geogebra.common.properties.impl.AbstractRangeProperty;
import org.geogebra.common.properties.impl.objects.delegate.GeoElementDelegate;
import org.geogebra.common.properties.impl.objects.delegate.NotApplicablePropertyException;
import org.geogebra.common.properties.impl.objects.delegate.TablePropertyDelegate;

public class CellBorderThicknessProperty extends AbstractRangeProperty<Integer> {
	private final GeoElementDelegate delegate;

	/**
	 * Constructor
	 * @param localization - localization
	 * @param element - geo
	 * @throws NotApplicablePropertyException - exception
	 */
	public CellBorderThicknessProperty(Localization localization, GeoElement element)
			throws NotApplicablePropertyException {
		super(localization, "Thickness", 1, 3, 2);
		delegate = new TablePropertyDelegate(element);
	}

	@Override
	protected void setValueSafe(Integer value) {
		InlineTableController formatter = getFormatter();
		formatter.setBorderThickness(value);
		delegate.getElement().updateVisualStyleRepaint(GProperty.COMBINED);
	}

	@Override
	public Integer getValue() {
		InlineTableController formatter = getFormatter();
		return formatter.getBorderThickness();
	}

	private InlineTableController getFormatter() {
		GeoInlineTable table = (GeoInlineTable) delegate.getElement();
		return (InlineTableController) table.getFormatter();
	}
}
