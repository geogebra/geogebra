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

	/**
	 * Creates an AbstractTableValuesColumn
	 * @param element evaluatable
	 * @param initialSize size of the cache
	 */
	public AbstractTableValuesColumn(GeoEvaluatable element, int initialSize) {
		this.element = element;
		this.kernel = element.getKernel();
		invalidateValues(initialSize);
	}

	@Override
	public Double getDoubleValue(int row) {
		if (doubleValues == null || doubleValues.length <= row) {
			return null;
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
			Double doubleValue = getDoubleValue(row);
			value = formatValue(doubleValue);
			stringValues[row] = value;
		}
		return value;
	}

	private String formatValue(Double value) {
		if (value == null) {
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

	@Override
	public void invalidateValue(int row) {
		doubleValues[row] = null;
		stringValues[row] = null;
	}

	protected abstract Double calculateValue(int row);

	protected abstract String getHeaderName();
}
