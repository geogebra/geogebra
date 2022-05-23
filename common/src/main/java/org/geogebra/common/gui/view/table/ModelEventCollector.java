package org.geogebra.common.gui.view.table;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.kernelND.GeoEvaluatable;

/**
 * Collects events published by the table values, then calls the listeners at the end of an update.
 * Handles nested {@link TableValuesView#startBatchUpdate()} and
 * {@link TableValuesView#endBatchUpdate()} calls. Discards all events when receives a
 * {@link TableValuesListener#notifyDatasetChanged(TableValuesModel)} call.
 */
public class ModelEventCollector implements TableValuesListener {

	private ModelEvent event = new ModelEvent();

	@Override
	public void notifyColumnRemoved(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		event.columnsRemoved.add(new ColumnEvent(column, evaluatable));
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		event.columnsChanged.add(new ColumnEvent(column, evaluatable));
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, GeoEvaluatable evaluatable, int column) {
		event.columnsAdded.add(new ColumnEvent(column, evaluatable));
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, GeoEvaluatable evaluatable,
			int column) {
		// Ignore
	}

	@Override
	public void notifyCellChanged(TableValuesModel model, GeoEvaluatable evaluatable, int column,
			int row) {
		event.cellsChanged.add(new CellEvent(column, row, evaluatable));
	}

	@Override
	public void notifyRowsRemoved(TableValuesModel model, int firstRow, int lastRow) {
		// Ignore
	}

	@Override
	public void notifyRowsAdded(TableValuesModel model, int firstRow, int lastRow) {
		// Ignore
	}

	@Override
	public void notifyRowChanged(TableValuesModel model, int row) {
		event.rowsChanged.add(row);
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		event.datasetChanged = true;
	}

	/**
	 * Initializes the collector and starts collecting events.
	 * Multiple calls to this instance is allowed. Caller must make sure
	 * to call {@link ModelEventCollector#endCollection(SimpleTableValuesModel)}.
	 * @param model model
	 */
	public void startCollection(TableValuesModel model) {
		if (event.counter == 0) {
			event.initialRowCount = model.getRowCount();
		}
		event.counter++;
	}

	/**
	 * Ends the collection of events and fires notifications. Caller must make sure to first call
	 * {@link ModelEventCollector#startCollection(TableValuesModel)} before this.
	 * @param model model
	 */
	public void endCollection(SimpleTableValuesModel model) {
		event.counter--;
		if (event.counter == 0) {
			fireModificationEvents(model);
		}
	}

	/**
	 * @return True, if it's called between
	 * {@link ModelEventCollector#startCollection(TableValuesModel)} and
	 * {@link ModelEventCollector#endCollection(SimpleTableValuesModel)} calls.
	 * Otherwise, false.
	 */
	public boolean isCollecting() {
		return event.counter > 0;
	}

	private void fireModificationEvents(SimpleTableValuesModel model) {
		ModelEvent eventClone = event.copy();
		clearModificationEvents();

		if (eventClone.datasetChanged && changeEventReceived(eventClone, model)) {
			model.notifyDatasetChanged();
		} else {
			fireAllModificationEvents(model, eventClone);
		}
	}

	private boolean changeEventReceived(ModelEvent event, SimpleTableValuesModel model) {
		if (event.cellsChanged.isEmpty() && event.columnsChanged.isEmpty()
				&& event.columnsRemoved.isEmpty() && event.columnsAdded.isEmpty()
				&& event.rowsChanged.isEmpty()) {
			int newRowCount = model.getRowCount();
			return newRowCount != event.initialRowCount;
		}
		return true;
	}

	private void fireAllModificationEvents(SimpleTableValuesModel model, ModelEvent event) {
		for (ColumnEvent columnEvent : event.columnsRemoved) {
			model.notifyColumnRemoved(columnEvent.evaluatable, columnEvent.columnIndex);
		}
		for (ColumnEvent columnEvent : event.columnsAdded) {
			model.notifyColumnAdded(columnEvent.evaluatable, columnEvent.columnIndex);
		}
		for (ColumnEvent columnEvent : event.columnsChanged) {
			model.notifyColumnChanged(columnEvent.evaluatable, columnEvent.columnIndex);
		}
		int newRowCount = model.getRowCount();
		if (newRowCount < event.initialRowCount) {
			model.notifyRowsRemoved(newRowCount, event.initialRowCount - 1);
			event.rowsChanged.removeIf(row -> row >= newRowCount);
		} else if (newRowCount > event.initialRowCount) {
			model.notifyRowsAdded(event.initialRowCount, newRowCount - 1);
			event.rowsChanged.removeIf(row -> row >= event.initialRowCount);
		}
		for (int row : event.rowsChanged) {
			model.notifyRowChanged(row);
		}
		for (CellEvent cellEvent : event.cellsChanged) {
			model.notifyCellChanged(cellEvent.evaluatable, cellEvent.columnIndex,
					cellEvent.rowIndex);
		}
	}

	private void clearModificationEvents() {
		event = new ModelEvent();
	}

	private static class ModelEvent {
		private final List<ColumnEvent> columnsRemoved = new ArrayList<>();
		private final List<ColumnEvent> columnsChanged = new ArrayList<>();
		private final List<ColumnEvent> columnsAdded = new ArrayList<>();
		private final List<CellEvent> cellsChanged = new ArrayList<>();
		private final List<Integer> rowsChanged = new ArrayList<>();
		private int initialRowCount;
		private boolean datasetChanged = false;
		private int counter = 0;

		public ModelEvent copy() {
			ModelEvent event = new ModelEvent();
			event.columnsRemoved.addAll(columnsRemoved);
			event.columnsChanged.addAll(columnsChanged);
			event.columnsAdded.addAll(columnsAdded);
			event.cellsChanged.addAll(cellsChanged);
			event.rowsChanged.addAll(rowsChanged);
			event.initialRowCount = initialRowCount;
			event.datasetChanged = datasetChanged;
			event.counter = counter;
			return event;
		}
	}

	private static class ColumnEvent {
		final int columnIndex;
		final GeoEvaluatable evaluatable;

		ColumnEvent(int columnIndex, GeoEvaluatable evaluatable) {
			this.columnIndex = columnIndex;
			this.evaluatable = evaluatable;
		}
	}

	private static class CellEvent {
		final int columnIndex;
		final int rowIndex;
		final GeoEvaluatable evaluatable;

		CellEvent(int columnIndex, int rowIndex, GeoEvaluatable evaluatable) {
			this.columnIndex = columnIndex;
			this.rowIndex = rowIndex;
			this.evaluatable = evaluatable;
		}
	}
}
