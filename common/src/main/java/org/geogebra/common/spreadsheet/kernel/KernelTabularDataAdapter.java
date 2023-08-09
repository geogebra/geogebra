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
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;

/**
 * Listens to changes of spreadsheet data (=GeoElements) in Kernel and passes
 * relevant notifications to Spreadsheet component.
 */
public class KernelTabularDataAdapter implements UpdateLocationView, TabularData {
	private final Map<Integer, Map<Integer, Object>> data = new HashMap<>();
	private final List<TabularDataChangeListener> changeListeners = new ArrayList<>();

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
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(geo.getLabelSimple());
		if (pt != null) {
			setContent(pt.y, pt.x, null);
			changeListeners.forEach(listener -> listener.update(pt.y, pt.x));
		}
	}

	@Override
	public void rename(GeoElement geo) {
		remove(geo);
		add(geo);
	}

	@Override
	public void update(GeoElement geo) {
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(geo.getLabelSimple());
		if (pt != null) {
			setContent(pt.y, pt.x, geo);
			changeListeners.forEach(listener -> listener.update(pt.y, pt.x));
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
		changeListeners.forEach(listener -> listener.update(-1, -1));
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
		return 200;
	}

	@Override
	public int numberOfColumns() {
		return 100;
	}

	@Override
	public void appendRows(int rows) {
		// TODO
	}

	@Override
	public void insertRowAt(int row) {
		// TODO
	}

	@Override
	public void deleteRowAt(int row) {
		// TODO
	}

	@Override
	public void appendColumns(int columns) {
		// TODO
	}

	@Override
	public void insertColumnAt(int column) {
		// TODO
	}

	@Override
	public void deleteColumnAt(int column) {
		// TODO
	}

	@Override
	public void setContent(int row, int column, Object content) {
		data.computeIfAbsent(row, ignore -> new HashMap<Integer, Object>()).put(column, content);
	}

	@Override
	public Object contentAt(int row, int column) {
		return data.get(row) != null ? data.get(row).get(column) : null;
	}

	@Override
	public String getColumnName(int column) {
		return GeoElementSpreadsheet.getSpreadsheetColumnName(column);
	}

	public void addChangeListener(TabularDataChangeListener changeListener) {
		changeListeners.add(changeListener);
	}
}
