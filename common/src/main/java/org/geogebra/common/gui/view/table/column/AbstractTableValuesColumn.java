package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

abstract public class AbstractTableValuesColumn implements TableValuesColumn {

	private final GeoEvaluatable element;
	private final Kernel kernel;
	private String[] stringValues;
	private Double[] doubleValues;
	private String header;

	public AbstractTableValuesColumn(GeoEvaluatable element) {
		this.element = element;
		this.kernel = element.getKernel();
	}

	@Override
	public double getDoubleValue(int row) {
		if (doubleValues == null || doubleValues.length <= row) {
			return Double.NaN;
		}
		Double value = doubleValues[row];
		if (value == null) {
			value = calculateValue(row);
			doubleValues[row] = value;
		}
		return value;
	}

	@Override
	public String getStringValue(int row) {
		if (stringValues == null || stringValues.length <= row) {
			return "";
		}
		String value = stringValues[row];
		if (value == null) {
			double doubleValue = getDoubleValue(row);
			value = formatValue(doubleValue);
			stringValues[row] = value;
		}
		return value;
	}

	private String formatValue(double value) {
		if (Double.isNaN(value)) {
			return "";
		}
		return kernel.format(value, StringTemplate.defaultTemplate);
	}

	@Override
	public String getHeader() {
		if (header == null) {
			header = getHeaderName();
		}
		return header;
	}

	@Override
	public GeoEvaluatable getEvaluatable() {
		return element;
	}

	@Override
	public void invalidateHeader() {
		header = null;
	}

	@Override
	public void invalidateValues(int size) {
		doubleValues = new Double[size];
		stringValues = new String[size];
	}

	protected abstract double calculateValue(int row);

	protected abstract String getHeaderName();
}
