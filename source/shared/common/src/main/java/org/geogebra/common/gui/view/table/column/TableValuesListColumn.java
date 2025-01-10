package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;

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
	protected double calculateValue(int row) {
		if (row >= list.size()) {
			return Double.NaN;
		}
		GeoElement element = list.get(row);
		return element.evaluateDouble();
	}

	@Override
	protected String getHeaderName() {
		return list.getLabelSimple();
	}

	@Override
	protected String getInputValue(int row) {
		if (row >= list.size()) {
			return "";
		}
		GeoElement element = list.get(row);
		if (element instanceof GeoText) {
			return ((GeoText) element).getTextString();
		}
		AlgoElement parentAlgo = element.getParentAlgorithm();
		if (parentAlgo != null && parentAlgo.getInput(0) instanceof GeoText) {
			return ((GeoText) parentAlgo.getInput(0)).getTextString();
		}
		return super.getInputValue(row);
	}
}

