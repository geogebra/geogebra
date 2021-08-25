package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;

public class TableValuesListColumn extends AbstractTableValuesColumn {

	private final GeoList list;

	/**
	 * Creates a list column.
	 * @param list list
	 */
	public TableValuesListColumn(GeoList list) {
		super(list);
		this.list = list;
	}

	@Override
	protected Double calculateValue(int row) {
		GeoElement element = list.get(row);
		double doubleValue = element.evaluateDouble();
		return Double.isNaN(doubleValue) ? null : doubleValue;
	}

	@Override
	protected String getHeaderName() {
		return list.getLabelSimple();
	}
}

