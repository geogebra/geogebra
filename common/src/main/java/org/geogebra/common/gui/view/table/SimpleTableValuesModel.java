package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.geogebra.common.gui.view.table.column.TableValuesColumn;
import org.geogebra.common.gui.view.table.column.TableValuesFunctionColumn;
import org.geogebra.common.gui.view.table.column.TableValuesListColumn;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.settings.TableSettings;
import org.geogebra.common.util.DoubleUtil;

import com.google.j2objc.annotations.Weak;

/**
 * TableValuesModel implementation. Uses caching to store values.
 */
final class SimpleTableValuesModel implements TableValuesModel {

	@Weak
	private final Kernel kernel;

	private final List<TableValuesListener> listeners;
	private final List<TableValuesColumn> columns;

	private GeoList[] importColumns;
	/** The user-visible column names. */
	private String[] columnNames;
	/** The internal labels: x_{1}, y_{1}, y_{2}, ... */
	private String[] columnLabels;

	private boolean allowsAddingColumns = true;
	private final TableSettings settings;

	private ModelEventCollector collector;
	private boolean isImportingData;
	private Runnable onDataImported;

	/**
	 * Construct a SimpleTableValuesModel.
	 * @param kernel kernel
	 */
	SimpleTableValuesModel(Kernel kernel, TableSettings settings) {
		this.kernel = kernel;
		this.settings = settings;
		this.listeners = new ArrayList<>();
		this.columns = new ArrayList<>();
		this.collector = new ModelEventCollector();
		GeoList values = getValueList();
		if (values.size() == 0 && settings.getValuesStep() != 0) {
			fillValueList(values);
			settings.resetMinMaxStep();
		}
		initializeModel();
	}

	private void fillValueList(GeoList values) {
		double[] range = DoubleUtil.range(settings.getValuesMin(),
				settings.getValuesMax(), settings.getValuesStep());
		for (Double d: range) {
			values.add(new GeoNumeric(kernel.getConstruction(), d));
		}
	}

	@Override
	public void registerListener(TableValuesListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(TableValuesListener listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean allowsAddingColumns() {
		return allowsAddingColumns;
	}

	@Override
	public void setAllowsAddingColumns(boolean allowsAddingColumns) {
		this.allowsAddingColumns = allowsAddingColumns;
	}

	@Override
	public boolean hasEditableColumns() {
		for (int column = 0; column < getColumnCount(); column++) {
			if (isColumnEditable(column)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isColumnEditable(int index) {
		if (index < 0 || index >= columns.size()) {
			return false;
		}
		if (index == 0) {
			return true; // the list of x-values is always editable
		}
		TableValuesColumn column = columns.get(index);
		GeoEvaluatable evaluatable = column.getEvaluatable();
		if (evaluatable == null) {
			return false;
		}
		return !(evaluatable instanceof GeoFunctionable);
	}

	@Override
	public int getRowCount() {
		int rowCount = 0;
		for (TableValuesColumn column : columns) {
			GeoEvaluatable evaluatable = column.getEvaluatable();
			if (evaluatable instanceof GeoList) {
				GeoList list = (GeoList) evaluatable;
				rowCount = Math.max(rowCount, list.size());
			}
		}
		return rowCount;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public TableValuesCell getCellAt(int row, int column) {
		return columns.get(column).getCellValue(row);
	}

	@Override
	public double getValueAt(int row, int column) {
		return columns.get(column).getDoubleValue(row);
	}

	@Override
	public String getHeaderAt(int column) {
		if (column == 0) {
			// we "override" the user-visible name of the x column,
			// because "x" cannot be used as a label
			return settings.getValueListCaption();
		}
		return columns.get(column).getHeader();
	}

	@Override
	public void setValuesHeader(String valuesHeader) {
		this.settings.setValueListCaption(valuesHeader);
	}

	/**
	 * Add an evaluatable to the model.
	 * @param evaluatable evaluatable
	 */
	void addEvaluatable(GeoEvaluatable evaluatable) {
		if (getEvaluatableIndex(evaluatable) == -1) {
			collector.startCollection(this);
			int idx = 0;
			while (idx < columns.size() && columns.get(idx)
					.getEvaluatable().getTableColumn() < evaluatable.getTableColumn()) {
				idx++;
			}
			TableValuesColumn column = createColumn(evaluatable);
			column.notifyDatasetChanged(this);
			columns.add(idx, column);
			ensureIncreasingIndices(idx);
			collector.notifyColumnAdded(this, evaluatable, idx);
			collector.endCollection(this);
		}
	}

	private TableValuesColumn createColumn(GeoEvaluatable evaluatable) {
		if (evaluatable.isGeoList()) {
			GeoList list = (GeoList) evaluatable;
			return new TableValuesListColumn(list);
		}
		return new TableValuesFunctionColumn(evaluatable, getValueList());
	}

	private void ensureIncreasingIndices(int idx) {
		int lastColumn = columns.get(idx).getEvaluatable().getTableColumn();
		for (int i = idx + 1; i < columns.size(); i++) {
			if (columns.get(i).getEvaluatable().getTableColumn() <= lastColumn) {
				lastColumn++;
				columns.get(i).getEvaluatable().setTableColumn(lastColumn);
			}
		}
	}

	/**
	 * Remove an evaluatable from the model.
	 * @param evaluatable evaluatable
	 */
	void removeEvaluatable(GeoEvaluatable evaluatable, boolean removedByUser) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			collector.startCollection(this);
			if (!kernel.getConstruction().isRemovingGeoToReplaceIt()) {
				evaluatable.setTableColumn(-1);
			}
			columns.remove(index);
			for (int i = 0; i < columns.size(); i++) {
				columns.get(i).getEvaluatable().setTableColumn(i);
			}
			collector.notifyColumnRemoved(this, evaluatable, index);
			collector.endCollection(this);
		}
	}

	/**
	 * Update the column for the Evaluatable object.
	 * @param evaluatable object to update in table
	 */
	void updateEvaluatable(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			collector.startCollection(this);
			collector.notifyColumnChanged(this, evaluatable, index);
			collector.endCollection(this);
		}
	}

	/**
	 * Optionally updates a cell of a column
	 * @param element element that might be part of a list
	 */
	void maybeUpdateListElement(GeoElement element) {
		collector.startCollection(this);
		for (int column = 0; column < columns.size(); column++) {
			GeoEvaluatable evaluatable = columns.get(column).getEvaluatable();
			if (!(evaluatable instanceof GeoList)) {
				continue;
			}
			GeoList list = (GeoList) evaluatable;
			int row = list.find(element);
			if (row <= -1) {
				continue;
			}
			collector.notifyCellChanged(this, evaluatable, column, row);
		}
		collector.endCollection(this);
	}

	/**
	 * Returns the index of the evaluatable in the model
	 * or -1 if it's not in the model.
	 * @param evaluatable object to check
	 * @return index of the object, -1 if it's not present
	 */
	int getEvaluatableIndex(GeoEvaluatable evaluatable) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getEvaluatable() == evaluatable) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Get the evaluatable from the model.
	 * @param index index of the object
	 * @return evaluatable if present in the model
	 */
	GeoEvaluatable getEvaluatable(int index) {
		if (index < columns.size() && index > -1) {
			return columns.get(index).getEvaluatable();
		}
		return null;
	}

	/**
	 * Update the name of the Evaluatable object (if it has any)
	 * @param evaluatable the evaluatable object
	 */
	void updateEvaluatableName(GeoEvaluatable evaluatable) {
		int index = getEvaluatableIndex(evaluatable);
		if (index > -1) {
			notifyColumnHeaderChanged(evaluatable, index);
		}
	}

	/**
	 * Set the x-values of the model.
	 * @param valuesArray x-values
	 */
	void setValues(double[] valuesArray) {
		collector.startCollection(this);
		GeoList values = getValueList();
		values.clear();
		for (double value : valuesArray) {
			values.add(new GeoNumeric(kernel.getConstruction(), value));
		}
		updateEvaluatable(values);
		collector.notifyDatasetChanged(this);
		collector.endCollection(this);
	}

	public GeoList getValueList() {
		if (settings.getValueList() == null) {
			GeoList xValues = setupXValues(new GeoList(kernel.getConstruction()));
			settings.setValueList(xValues);
		}
		return settings.getValueList();
	}

	@Override
	public GeoList setupXValues(GeoList xValues) {
		xValues.setAuxiliaryObject(true);
		xValues.setCanBeRemovedAsInput(false);
		return xValues;
	}

	private void initializeModel() {
		TableValuesColumn column = new TableValuesListColumn(getValueList());
		columns.add(column);
		column.notifyDatasetChanged(this);
	}

	@Override
	public void removeAllColumns() {
		for (int i = columns.size() - 1; i >= 0; i--) {
			columns.get(i).getEvaluatable().remove();
		}
	}

	/**
	 * Clears and initializes the model.
	 */
	void clearModel() {
		columns.clear();
		initializeModel();
		collector.notifyDatasetChanged(this);
	}

	@Override
	public void startBatchUpdate() {
		collector.startCollection(this);
	}

	@Override
	public void endBatchUpdate(boolean notifyDatasetChanged) {
		if (notifyDatasetChanged) {
			collector.notifyDatasetChanged(this);
		}
		collector.endCollection(this);
	}

	@Override
	public boolean isBatchUpdate() {
		return collector.isCollecting();
	}

	void notifyColumnRemoved(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnRemoved(this, evaluatable, column));
	}

	void notifyColumnAdded(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnAdded(this, evaluatable, column));
	}

	void notifyColumnChanged(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnChanged(this, evaluatable, column));
	}

	void notifyColumnHeaderChanged(GeoEvaluatable evaluatable, int column) {
		forEachListener(listener -> listener.notifyColumnHeaderChanged(this, evaluatable, column));
	}

	void notifyCellChanged(GeoEvaluatable evaluatable, int column, int row) {
		forEachListener(listener -> listener.notifyCellChanged(this, evaluatable, column, row));
	}

	void notifyRowsRemoved(int firstRow, int lastRow) {
		forEachListener(listener -> listener.notifyRowsRemoved(this, firstRow, lastRow));
	}

	void notifyRowsAdded(int firstRow, int lastRow) {
		forEachListener(listener -> listener.notifyRowsAdded(this, firstRow, lastRow));
	}

	void notifyRowChanged(int row) {
		forEachListener(listener -> listener.notifyRowChanged(this, row));
	}

	void notifyDatasetChanged() {
		forEachListener(listener -> listener.notifyDatasetChanged(this));
		if (onDataImported != null) {
			onDataImported.run();
			onDataImported = null;
		}
	}

	private void notifyImportFinished() {
		forEachListener(listener -> listener.notifyImportFinished(this));
	}

	private Stream<TableValuesListener> listenerStream() {
		return Stream.concat(columns.stream(), listeners.stream());
	}

	private void forEachListener(Consumer<? super TableValuesListener> action) {
		listenerStream().forEachOrdered(action);
	}

	@Override
	public void set(GeoElement element, GeoList column, int rowIndex) {
		int columnIndex = getEvaluatableIndex(column);
		if (columnIndex == -1) {
			return;
		}
		collector.startCollection(this);
		ensureCapacity(column, rowIndex);
		column.setListElement(rowIndex, element);
		column.setDefinition(null);
		if (isEmptyValue(element)) {
			handleEmptyValue(column, columnIndex, rowIndex);
		} else if (column == getValueList()) {
			collector.notifyRowChanged(this, rowIndex);
		} else if (getEvaluatableIndex(column) > -1 && column.listContains(element)) {
			element.notifyUpdate();
		}
		collector.endCollection(this);
	}

	private void ensureCapacity(GeoList list, int index) {
		list.ensureCapacity(index + 1);
		for (int i = list.size(); i < index + 1; i++) {
			list.add(createEmptyValue());
		}
	}

	@Override
	public boolean isEmptyValue(GeoElement element) {
		// empty texts may be saved in files before APPS-4468
		return (element instanceof GeoText && "".equals(((GeoText) element).getTextString()))
				|| (element.isGeoNumeric() && element.isIndependent() && !element.isDefined());
	}

	@Override
	public GeoElement createEmptyValue() {
		return new GeoNumeric(kernel.getConstruction(), Double.NaN);
	}

	@Override
	public GeoNumeric createValue(double value) {
		return new GeoNumeric(kernel.getConstruction(), value);
	}

	private void handleEmptyValue(GeoList column, int columnIndex, int rowIndex) {
		if (rowIndex == column.size() - 1) {
			removeEmptyRows(columnIndex);
		} else if (column == getValueList()) {
			collector.notifyRowChanged(this, rowIndex);
		} else if (isColumnEmpty(column)) {
			column.remove();
		} else {
			collector.notifyCellChanged(this, column, columnIndex, rowIndex);
		}
	}

	private boolean isColumnEmpty(GeoList column) {
		for (int i = 0; i < column.size(); i++) {
			GeoElement element = column.get(i);
			if (!isEmptyValue(element)) {
				return false;
			}
		}
		return true;
	}

	private void removeEmptyRows(int columnIndex) {
		TableValuesColumn tableValuesColumn = columns.get(columnIndex);
		GeoEvaluatable evaluatable = tableValuesColumn.getEvaluatable();
		if (!(evaluatable instanceof GeoList)) {
			return;
		}
		GeoList column = (GeoList) evaluatable;
		while (column.size() > 0 && isEmptyValue(column.get(column.size() - 1))) {
			int row = column.size() - 1;
			column.remove(row);
			if (columnIndex == 0) {
				collector.notifyRowChanged(this, row);
			} else {
				collector.notifyCellChanged(this, evaluatable, columnIndex, row);
			}
		}

		if (columnIndex != 0 && isColumnEmpty(column)) {
			column.remove();
		}
	}

	public boolean isEvaluatableEmptyList(int column) {
		return getEvaluatable(column).isGeoList()
				&& ((GeoList) getEvaluatable(column)).isEmptyList();
	}

	/**
	 * Update values column from the settings
	 */
	public void updateValuesColumn() {
		TableValuesListColumn element = new TableValuesListColumn(getValueList());
		columns.set(0, element);
		element.notifyColumnChanged(this, getValueList(), 0);
	}

	/**
	 * Prepares for tabular data import.
	 * @param nrRows The number of rows to import.
	 * @param nrColumns The number of columns to import.
	 * @param columnNames The user-visible column names (optional, can be null).
	 */
	// Data import
	public void startImport(int nrRows, int nrColumns, String[] columnNames) {
		if (nrColumns < 1) {
			return;
		}
		importColumns = new GeoList[nrColumns];
		this.columnNames = columnNames;
		this.columnLabels = new String[nrColumns];
		for (int columnIdx = 0; columnIdx < nrColumns; columnIdx++) {
			columnLabels[columnIdx] = columnIdx == 0 ? "x_{1}" : "y_{" + columnIdx + "}";
			GeoList list = new GeoList(kernel.getConstruction());
			list.setAuxiliaryObject(true);
			importColumns[columnIdx] = list;
		}
	}

	/**
	 * Imports a row of data.
	 * @param values The numeric values for the current row. For any null entries in
	 *               this array, rawValues will have the original string value.
	 * @param rawValues The original strings behind the values.
	 */
	public void importRow(Double[] values, String[] rawValues) {
		if (importColumns == null) {
			return;
		}
		for (int index = 0; index < importColumns.length; index++) {
			GeoList column = importColumns[index];
			GeoElement element = null;
			if (index < values.length) {
				if (values[index] != null) {
					element = new GeoNumeric(kernel.getConstruction(), values[index], false);
				} else {
					element = new GeoText(kernel.getConstruction(), rawValues[index], false);
				}
			} else {
				// create an empty value
				element = new GeoNumeric(kernel.getConstruction(), Double.NaN, false);
			}
			column.add(element);
		}
	}

	/**
	 * Cancels import, discarding any data accumulated in {@link #importRow(Double[], String[])}.
	 */
	public void cancelImport() {
		importColumns = null;
		columnLabels = null;
		columnNames = null;
	}

	/**
	 * Commits the data accumulated in {@link #importRow(Double[], String[])}, creating
	 * columns, and notifying listeners about the new data.
	 */
	public void commitImport() {
		importColumns();
		importColumns = null;
		columnLabels = null;
		columnNames = null;
	}

	private void importColumns() {
		if (importColumns.length == 0 || columnLabels.length != importColumns.length) {
			return;
		}
		isImportingData = true;
		collector.startCollection(this);
		removeXColumn();
		removeYColumns();
		columns.clear();
		importXColumn();
		importYColumns();
		kernel.storeUndoInfo();
		collector.notifyDatasetChanged(this);
		collector.endCollection(this);
		notifyImportFinished();
		isImportingData = false;
	}

	@Override
	public boolean isImportingData() {
		return isImportingData;
	}

	@Override
	public void setOnDataImportedRunnable(Runnable onDataImported) {
		this.onDataImported = onDataImported;
	}

	private void importXColumn() {
		GeoList values = importColumns[0];
		setupXValues(values);
		String label = columnLabels[0];
		String name = columnNames != null && columnNames[0] != null ? columnNames[0] : "x";
		// note: setting the label has the side effect of adding
		// the column to the construction!
		values.setLabel(label);
		settings.setValueList(values);
		settings.setValueListCaption(name);
		TableValuesColumn valuesColumn = new TableValuesListColumn(values);
		columns.add(valuesColumn);
	}

	private void importYColumns() {
		for (int columnIdx = 1; columnIdx < importColumns.length; columnIdx++) {
			GeoList values = importColumns[columnIdx];
			String label = columnLabels[columnIdx];
			// Note: setting the label has the side effect of adding the column to the construction!
			values.setLabel(label);
			// Note: The column names for the y columns are not yet used. This will need further
			// work if we want to support importing column names into the TableValuesView/Model.
			values.setTableColumn(columnIdx);
			values.setPointsVisible(false);
			TableValuesColumn column = new TableValuesListColumn(values);
			columns.add(column);
		}
	}

	private void removeXColumn() {
		GeoList xValues = settings.getValueList();
		kernel.getConstruction().removeFromConstructionList(xValues);
		collector.notifyColumnRemoved(this, xValues, 0);
		settings.setValueList(null);
	}

	private void removeYColumns() {
		for (int columnIdx = 1; columnIdx < columns.size(); columnIdx++) {
			TableValuesColumn column = columns.get(columnIdx);
			collector.notifyColumnRemoved(this, column.getEvaluatable(), 0);
			column.getEvaluatable().setTableColumn(-1);
		}
	}
}
