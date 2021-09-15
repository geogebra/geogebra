package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public class TableValuesFunctionColumn extends AbstractTableValuesColumn {

	private final GeoEvaluatable evaluatable;
	private final GeoList values;

	/**
	 * Creates a function column
	 * @param evaluatable function
	 * @param values values to evaluate function at
	 * @param initialSize size of the cache
	 */
	public TableValuesFunctionColumn(GeoEvaluatable evaluatable, GeoList values, int initialSize) {
		super(evaluatable, initialSize);
		this.evaluatable = evaluatable;
		this.values = values;
	}

	@Override
	protected double calculateValue(int row) {
		double xValue = values.get(row).evaluateDouble();
		return evaluatable.value(xValue);
	}

	@Override
	protected String getHeaderName() {
		return evaluatable.getLabelSimple() + "(x)";
	}
}
