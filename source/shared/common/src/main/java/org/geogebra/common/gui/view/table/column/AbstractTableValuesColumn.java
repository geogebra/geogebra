package org.geogebra.common.gui.view.table.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.geogebra.common.gui.view.table.TableValuesCell;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.util.StringUtil;

abstract public class AbstractTableValuesColumn implements TableValuesColumn {

	private final GeoEvaluatable element;
	private final Kernel kernel;
	private ArrayList<TableValuesCell> cells;
	private ArrayList<Double> doubleValues;
	private String header;

	/**
	 * Creates an AbstractTableValuesColumn
	 * @param element evaluatable
	 */
	public AbstractTableValuesColumn(GeoEvaluatable element) {
		this.element = element;
		this.kernel = element.getKernel();
	}

	@Override
	public double getDoubleValue(int row) {
		if (doubleValues == null || doubleValues.size() <= row) {
			return Double.NaN;
		}
		Double value = doubleValues.get(row);
		if (value == null) {
			value = calculateValue(row);
			doubleValues.set(row, value);
		}
		return value;
	}

	@Override
	public TableValuesCell getCellValue(int row) {
		if (cells == null || cells.size() <= row) {
			return new TableValuesCell("", false);
		}
		TableValuesCell cell = cells.get(row);
		if (cell == null) {
			cell = createTableValuesCell(row);
			cells.set(row, cell);
		}
		return cell;
	}

	private TableValuesCell createTableValuesCell(int row) {
		double doubleValue = getDoubleValue(row);
		boolean isErroneous = false;
		String input;
		if (Double.isNaN(doubleValue)) {
			input = getInputValue(row);
			if (input == null) {
				// the double value cannot be calculated for this x-value
				input = "";
			} else {
				// the input is erroneous or empty
				isErroneous = !StringUtil.isTrimmedEmpty(input);
			}
		} else {
			input = formatValue(doubleValue);
		}
		return new TableValuesCell(input, isErroneous);
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
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (evaluatable == element) {
			invalidateValues(model.getRowCount());
		}
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		// Ignore
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		if (evaluatable == element) {
			invalidateHeader();
		}
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		if (evaluatable == element) {
			invalidateValue(row);
		}
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		for (int row = firstRow; row <= lastRow; row++) {
			notifyRowRemoved(row);
		}
	}

	private void notifyRowRemoved(int row) {
		if (row >= doubleValues.size()) {
			return;
		}
		doubleValues.remove(row);
		cells.remove(row);
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		invalidateValue(row);
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		if (lastRow > doubleValues.size()) {
			Collection nullValues = Collections.nCopies(lastRow - doubleValues.size() + 1, null);
			doubleValues.addAll(nullValues);
			cells.addAll(nullValues);
		} else {
			doubleValues.add(lastRow, null);
			cells.add(lastRow, null);
		}
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		invalidateValues(model.getRowCount());
	}

	private void invalidateValues(int size) {
		doubleValues = new ArrayList<>(Collections.nCopies(size, null));
		cells = new ArrayList<>(Collections.nCopies(size, null));
	}

	private void invalidateValue(int row) {
		if (doubleValues.size() <= row) {
			return;
		}
		doubleValues.set(row, null);
		cells.set(row, null);
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
