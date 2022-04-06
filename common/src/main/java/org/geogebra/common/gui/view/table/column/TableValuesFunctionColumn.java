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
	 */
	public TableValuesFunctionColumn(GeoEvaluatable evaluatable, GeoList values) {
		super(evaluatable);
		this.evaluatable = evaluatable;
		this.values = values;
	}

	@Override
	protected double calculateValue(int row) {
		if (values.size() <= row) {
			return Double.NaN;
		}
		double xValue = values.get(row).evaluateDouble();
		return evaluatable.value(xValue);
	}

	@Override
	protected String getHeaderName() {
		return evaluatable.getLabelSimple() + "(x)";
	}
}
