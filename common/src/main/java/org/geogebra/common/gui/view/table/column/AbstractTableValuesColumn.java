package org.geogebra.common.gui.view.table.column;

import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.util.StringUtil;

abstract public class AbstractTableValuesColumn implements TableValuesColumn {

	private final GeoEvaluatable element;
	private final Kernel kernel;
	private TableValuesCell[] cells;
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
	public TableValuesCell getCellValue(int row) {
		if (cells == null || cells.length <= row) {
			return new TableValuesCell("", false);
		}
		TableValuesCell cell = cells[row];
		if (cell == null) {
			cell = createTableValuesCell(row);
			cells[row] = cell;
		}
		return cell;
	}

	private TableValuesCell createTableValuesCell(int row) {
		double doubleValue = getDoubleValue(row);
		boolean isErroneus = false;
		String input;
		if (Double.isNaN(doubleValue)) {
			input = getInputValue(row);
			if (input == null) {
				// the double value cannot be calculated for this x-value
				input = "";
			} else {
				// the input is erroneous or empty
				isErroneus = !StringUtil.isTrimmedEmpty(input);
			}
		} else {
			input = formatValue(doubleValue);
		}
		return new TableValuesCell(input, isErroneus);
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
		cells = new TableValuesCell[size];
	}

	@Override
	public void invalidateValue(int row) {
		doubleValues[row] = null;
		cells[row] = null;
	}

	/**
	 * Get the actual input value at the row index.
	 * @param row index
	 * @return the string value at the index or null if it's calculated.
	 */
	protected String getInputValue(int row) {
		return null;
	}

	/**
	 * Calculate the value at the row index.
	 * @param row index
	 * @return the value at row, or Double.NaN if the input is not a number.
	 */
	protected abstract double calculateValue(int row);

	protected abstract String getHeaderName();
}
