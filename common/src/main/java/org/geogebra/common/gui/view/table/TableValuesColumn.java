package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

public class TableValuesColumn {

	private final GeoEvaluatable evaluatable;
	private final GeoList xValues;

	private String headerName;
	private Double[] doubleValues;
	private String[] stringValues;

	TableValuesColumn(GeoEvaluatable evaluatable, GeoList xValues) {
		this.evaluatable = evaluatable;
		this.xValues = xValues;

		doubleValues = new Double[xValues.size()];
		stringValues = new String[xValues.size()];
	}

	boolean isModifiable() {
		return evaluatable instanceof GeoList;
	}

	GeoEvaluatable getEvaluatable() {
		return evaluatable;
	}

	int getTableColumn() {
		return evaluatable.getTableColumn();
	}

	void setTableColumn(int tableColumn) {
		evaluatable.setTableColumn(tableColumn);
	}

	String getCellAt(int row) {
		String value = stringValues[row];
		if (value == null) {
			double doubleValue = getValueAt(row);
			value = format(doubleValue);
			stringValues[row] = value;
		}
		return value;
	}

	double getValueAt(int row) {
		Double value = doubleValues[row];
		if (value == null) {
			value = evaluateAt(row);
			doubleValues[row] = value;
		}
		return value;
	}

	void clearCache() {
		doubleValues = new Double[xValues.size()];
		stringValues = new String[xValues.size()];
	}

	String getHeaderName() {
		if (headerName == null) {
			updateHeaderName();
		}

		return headerName;
	}

	void updateHeaderName() {
		String labelSimple = evaluatable.getLabelSimple();
		headerName = evaluatable.isGeoList() ? labelSimple : labelSimple + "(x)";
	}

	void setCell(int row, String content) {
		stringValues[row] = null;
		doubleValues[row] = null;
		GeoNumeric cell = (GeoNumeric) ((GeoList) evaluatable).get(row);
		cell.setValue(Double.parseDouble(content)); //TODO
	}

	private double evaluateAt(int row) {
		if (evaluatable.isGeoList()) {
			return evaluatable.value(row);
		}
		double x = xValues.get(row).evaluateDouble();
		return evaluatable.value(x);
	}

	private String format(double x) {
		return evaluatable.getKernel().format(x, StringTemplate.defaultTemplate);
	}

	/**
	 * Update and repaint all dependent geos (e.g. regression plots)
	 */
	public void updateRepaint() {
		if (isModifiable()) {
			evaluatable.updateRepaint();
		}
	}
}
