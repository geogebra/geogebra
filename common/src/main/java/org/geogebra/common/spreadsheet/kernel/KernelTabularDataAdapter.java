package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.spreadsheet.core.CellDragPasteHandler;
import org.geogebra.common.spreadsheet.core.PersistenceListener;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.SpreadsheetDimensions;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;
import org.geogebra.common.spreadsheet.core.TabularDataPasteGeos;
import org.geogebra.common.spreadsheet.core.TabularDataPasteInterface;
import org.geogebra.common.spreadsheet.style.CellFormat;

/**
 * Listens to changes of spreadsheet data (=GeoElements) in Kernel and passes
 * relevant notifications to Spreadsheet component.
 */
public final class KernelTabularDataAdapter implements UpdateLocationView, TabularData<GeoElement> {
	private final Map<Integer, Map<Integer, GeoElement>> data = new HashMap<>();
	private final List<TabularDataChangeListener> changeListeners = new ArrayList<>();
	private final KernelTabularDataProcessor processor;
	private final CellFormat cellFormat;
	private final SpreadsheetSettings spreadsheetSettings;
	private final Kernel kernel;

	/**
	 * @param spreadsheetSettings spreadsheet settings
	 */
	public KernelTabularDataAdapter(SpreadsheetSettings spreadsheetSettings, Kernel kernel) {
		this.cellFormat = new CellFormat(null);
		this.spreadsheetSettings = spreadsheetSettings;
		this.kernel = kernel;
		cellFormat.processXMLString(spreadsheetSettings.cellFormat());
		spreadsheetSettings.addListener((settings) -> {
				notifySizeChanged(spreadsheetSettings);
				cellFormat.processXMLString(spreadsheetSettings.cellFormat());
		});
		this.processor = new KernelTabularDataProcessor(this);
	}

	private void notifySizeChanged(SpreadsheetDimensions spreadsheetDimensions) {
		for (TabularDataChangeListener listener: changeListeners) {
			listener.tabularDataSizeDidChange(spreadsheetDimensions);
		}
	}

	@Override
	public void updateLocation(GeoElement geo) {
		// not needed
	}

	@Override
	public void add(GeoElement geo) {
		update(geo);
	}

	@Override
	public void remove(GeoElement geo) {
		removeByLabel(geo.getLabelSimple());
	}

	private void removeByLabel(String labelSimple) {
		SpreadsheetCoords pt = GeoElementSpreadsheet.spreadsheetIndices(labelSimple);
		if (pt != null && pt.column != -1) {
			setContent(pt.row, pt.column, null);
			changeListeners.forEach(listener -> listener.tabularDataDidChange(pt.row, pt.column));
		}
	}

	@Override
	public void rename(GeoElement geo) {
		removeByLabel(geo.getOldLabel());
		add(geo);
	}

	@Override
	public void update(GeoElement geo) {
		SpreadsheetCoords pt = GeoElementSpreadsheet.spreadsheetIndices(geo.getLabelSimple());
		if (pt.column != -1) {
			setContent(pt.row, pt.column, geo);
			changeListeners.forEach(listener -> listener.tabularDataDidChange(pt.row, pt.column));
		}
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {
		// TODO
	}

	@Override
	public void updateHighlight(GeoElementND geo) {
		// not needed
	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {
		// not needed
	}

	@Override
	public void repaintView() {
		// TODO
	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void reset() {
		// not needed
	}

	@Override
	public void clearView() {
		data.clear();
		changeListeners.forEach(listener -> listener.tabularDataDidChange(-1, -1));
	}

	@Override
	public void setMode(int mode, ModeSetter m) {
		// not needed
	}

	@Override
	public int getViewID() {
		return App.VIEW_SPREADSHEET;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void startBatchUpdate() {
		// TODO
	}

	@Override
	public void endBatchUpdate() {
		// TODO
	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {
		// not needed
	}

	@Override
	public void reset(int rows, int columns) {
		// TODO
	}

	@Override
	public int numberOfRows() {
		return this.spreadsheetSettings.getRows();
	}

	@Override
	public int numberOfColumns() {
		return this.spreadsheetSettings.getColumns();
	}

	@Override
	public void insertRowAt(int row) {
		spreadsheetSettings.setRowsNoFire(numberOfRows() + 1);
		processor.insertRowAt(row);
	}

	@Override
	public void deleteRowAt(int row) {
		spreadsheetSettings.setRowsNoFire(numberOfRows() - 1);
		processor.deleteRowAt(row);
	}

	@Override
	public void insertColumnAt(int column) {
		spreadsheetSettings.setColumnsNoFire(numberOfColumns() + 1);
		processor.insertColumnAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		spreadsheetSettings.setColumnsNoFire(numberOfColumns() - 1);
		processor.deleteColumnAt(column);
	}

	@Override
	public void setContent(int row, int column, Object content) {
		if (content != null) {
			GeoElement geo = (GeoElement) content;
			geo.rename(GeoElementSpreadsheet.getSpreadsheetCellName(column, row));
			data.computeIfAbsent(row, ignore -> new HashMap<>()).put(column, geo);
		} else {
			data.computeIfAbsent(row, ignore -> new HashMap<>()).put(column, null);
		}
	}

	@Override
	public GeoElement contentAt(int row, int column) {
		return data.get(row) != null ? data.get(row).get(column) : null;
	}

	@Override
	public String getColumnName(int column) {
		return GeoElementSpreadsheet.getSpreadsheetColumnName(column);
	}

	@Override
	public void addChangeListener(TabularDataChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	@Override
	public TabularDataPasteInterface<GeoElement> getPaste() {
		return new TabularDataPasteGeos();
	}

	@Override
	public CellFormat getFormat() {
		return cellFormat;
	}

	@Override
	public int getAlignment(int row, int column) {
		return cellFormat.getAlignment(column, row, contentAt(row, column) instanceof GeoText);
	}

	@Override
	public void setPersistenceListener(PersistenceListener layout) {
		spreadsheetSettings.setPersistenceListener(layout);
	}

	@Override
	public CellDragPasteHandler getCellDragPasteHandler() {
		return new CellDragPasteHandler(this, kernel);
	}
}