package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.SpreadsheetSettings;
import org.geogebra.common.spreadsheet.core.CellDragPasteHandler;
import org.geogebra.common.spreadsheet.core.SpreadsheetCellProcessor;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;
import org.geogebra.common.spreadsheet.core.TabularDataPasteInterface;

/**
 * Listens to changes of spreadsheet data (=GeoElements) in Kernel and passes
 * relevant notifications to Spreadsheet component.
 */
public final class KernelTabularDataAdapter implements UpdateLocationView, TabularData<GeoElement> {

	private final @Nonnull App app;
	private final @Nonnull Kernel kernel;
	private final @Nonnull KernelTabularDataProcessor processor;
	private final @Nonnull SpreadsheetCellProcessor cellProcessor;
	private final List<TabularDataChangeListener> changeListeners = new ArrayList<>();
	private final Map<Integer, Map<Integer, GeoElement>> data = new HashMap<>();

	/**
	 * @param app the App
	 */
	public KernelTabularDataAdapter(@Nonnull App app) {
		this.app = app;
		// careful: the SpreadsheetSettings instance may change at runtime, don't store a reference!
		SpreadsheetSettings spreadsheetSettings = app.getSettings().getSpreadsheet();
		// OK: the SpreadsheetSettings listeners are carried over when a new instance is created
		spreadsheetSettings.addListener((settings) -> {
			// changeListeners is really just the Spreadsheet instance
			for (TabularDataChangeListener listener: changeListeners) {
				listener.tabularDataDimensionsDidChange((SpreadsheetSettings) settings);
			}
		});
		this.kernel = app.getKernel();
		this.processor = new KernelTabularDataProcessor(this);
		this.cellProcessor = new DefaultSpreadsheetCellProcessor(kernel.getAlgebraProcessor());
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

	// Helpers
	
	private void removeByLabel(String labelSimple) {
		SpreadsheetCoords pt = GeoElementSpreadsheet.spreadsheetIndices(labelSimple);
		if (pt != null && pt.column != -1) {
			setContent(pt.row, pt.column, null);
			changeListeners.forEach(listener -> listener.tabularDataDidChange(pt.row, pt.column));
		}
	}

	private void unfixSymbolic(GeoElement geo) {
		if (geo instanceof GeoSymbolic && geo.isLocked()) {
			geo.setFixed(false);
		}
	}

	private void setLabel(GeoElement geo, int row, int column) {
		String label = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);
		if (kernel.getConstruction().isFreeLabel(label)) {
			geo.rename(label);
		}
	}

	/**
	 * Sets default visibility (false for texts, true for other objects) for graphics
	 * and auxiliary flag (always true) for AV .
	 * @see org.geogebra.common.gui.view.spreadsheet.RelativeCopy#setVisibilityFlags(GeoElementND) 
	 * @param geo the element to be modified
	 */
	public static void setEuclidianVisibilityAndAuxiliaryFlag(GeoElementND geo) {
		if (geo.isGeoText()) {
			geo.setEuclidianVisible(false);
		}
		geo.setAuxiliaryObject(true);
		geo.updateVisualStyle(GProperty.VISIBLE);
	}

	// -- HasTabularValues --

	@Override
	public @CheckForNull GeoElement contentAt(int row, int column) {
		return data.get(row) != null ? data.get(row).get(column) : null;
	}

	@Override
	public int numberOfRows() {
		return app.getSettings().getSpreadsheet().getRows();
	}

	@Override
	public int numberOfColumns() {
		return app.getSettings().getSpreadsheet().getColumns();
	}

	// -- TabularData --

	@Override
	public @Nonnull SpreadsheetCellProcessor getCellProcessor() {
		return cellProcessor;
	}

	@Override
	public TabularDataPasteInterface<GeoElement> getPaste() {
		return new TabularDataPasteGeos(kernel);
	}

	@Override
	public CellDragPasteHandler getCellDragPasteHandler() {
		return new KernelCellDragPasteHandler(this, kernel);
	}

	@Override
	public void addChangeListener(@Nonnull TabularDataChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	@Override
	public void insertRowAt(int row) {
		app.getSettings().getSpreadsheet().setRowsNoFire(numberOfRows() + 1);
		processor.insertRowAt(row);
	}

	@Override
	public void deleteRowAt(int row) {
		app.getSettings().getSpreadsheet().setRowsNoFire(numberOfRows() - 1);
		processor.deleteRowAt(row);
	}

	@Override
	public void insertColumnAt(int column) {
		app.getSettings().getSpreadsheet().setColumnsNoFire(numberOfColumns() + 1);
		processor.insertColumnAt(column);
	}

	@Override
	public void deleteColumnAt(int column) {
		app.getSettings().getSpreadsheet().setColumnsNoFire(numberOfColumns() - 1);
		processor.deleteColumnAt(column);
	}

	@Override
	public void setContent(int row, int column, Object content) {
		if (content != null) {
			GeoElement geo = (GeoElement) content;
			unfixSymbolic(geo);
			setLabel(geo, row, column);
			data.computeIfAbsent(row, ignore -> new HashMap<>()).put(column, geo);
			if (numberOfRows() <= row) {
				app.getSettings().getSpreadsheet().setRowsNoFire(row + 1);
			}
			if (numberOfColumns() <= column) {
				app.getSettings().getSpreadsheet().setColumnsNoFire(column + 1);
			}
		} else {
			data.computeIfAbsent(row, ignore -> new HashMap<>()).put(column, null);
		}
	}

	@Override
	public void removeContentAt(int row, int column) {
		processor.removeContentAt(row, column);
		setContent(row, column, null);
	}

	@Override
	public boolean isTextContentAt(int row, int column) {
		return contentAt(row, column) instanceof GeoText;
	}

	@Override
	public void markNonEmpty(int row, int column) {
		GeoElement geo = contentAt(row, column);
		if (geo != null) {
			geo.setEmptySpreadsheetCell(false);
		}
	}

	@Override
	public @Nonnull String serializeContentAt(int row, int column) {
		GeoElement geoElement = contentAt(row, column);
		return geoElement == null ? ""
				: geoElement.getRedefineString(true, false);
	}

	@Override
	public boolean hasError(int row, int column) {
		if (data.get(row) == null || data.get(row).get(column) == null) {
			return false;
		}
		GeoElement geo = data.get(row).get(column);
		return !geo.isDefined() && !geo.isEmptySpreadsheetCell();
	}

	@Override
	public String getErrorString() {
		return kernel.getLocalization().getError("Error").toUpperCase(Locale.ROOT);
	}
}
