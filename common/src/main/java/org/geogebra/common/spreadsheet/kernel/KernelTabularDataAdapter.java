package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
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

	/**
	 * @param spreadsheetSettings spreadsheet settings
	 */
	public KernelTabularDataAdapter(SpreadsheetSettings spreadsheetSettings) {
		this.cellFormat = new CellFormat(null);
		cellFormat.processXMLString(spreadsheetSettings.cellFormat());
		spreadsheetSettings.addListener((settings) ->
				cellFormat.processXMLString(spreadsheetSettings.cellFormat()));
		this.processor = new KernelTabularDataProcessor(this);
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
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(labelSimple);
		if (pt != null && pt.x != -1) {
			setContent(pt.y, pt.x, null);
			changeListeners.forEach(listener -> listener.tabularDataDidChange(pt.y, pt.x));
		}
	}

	@Override
	public void rename(GeoElement geo) {
		removeByLabel(geo.getOldLabel());
		add(geo);
	}

	@Override
	public void update(GeoElement geo) {
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(geo.getLabelSimple());
		if (pt.x != -1) {
			setContent(pt.y, pt.x, geo);
			changeListeners.forEach(listener -> listener.tabularDataDidChange(pt.y, pt.x));
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
		return 100;
	}

	@Override
	public int numberOfColumns() {
		return 26;
	}

	@Override
	public void insertRowAt(int row) {
		processor.insertRowAt(row);
	}

	@Override
	public void deleteRowAt(int row) {
		processor.deleteRowAt(row);
	}

	@Override
	public void insertColumnAt(int column) {
		processor.insertColumnAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
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
	public void copyPasteContent(int sourceRow, int targetRow, int sourceColumn, int targetColumn) {
		GeoElement geoToCopy = contentAt(sourceRow, sourceColumn);
		if (geoToCopy == null) {
			return;
		}
		SpreadsheetCellProcessor cellProcessor = new SpreadsheetCellProcessor(
				GeoElementSpreadsheet.getSpreadsheetCellName(targetColumn, targetRow),
				geoToCopy.getKernel().getAlgebraProcessor(),
				geoToCopy.getKernel().getApplication().getDefaultErrorHandler());
		if (cellProcessor.containsDynamicReference(geoToCopy)) {
			String definition = geoToCopy.getDefinitionForEditor();
			cellProcessor.process(definition.substring(definition.indexOf('=')), false);
			return;
		}

		GeoElement copy = geoToCopy.copy();
		copy.setLabel(GeoElementSpreadsheet.getSpreadsheetCellName(targetColumn, targetRow));
		copy.setDefinition(geoToCopy.getDefinition());
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
}
