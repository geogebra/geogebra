package org.geogebra.common.spreadsheet.kernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.Spreadsheet;
import org.geogebra.common.spreadsheet.core.TabularData;
import org.geogebra.common.spreadsheet.core.TabularDataChangeListener;

/**
 * Listens to changes of spreadsheet data (=GeoElements) in Kernel and passes
 * relevant notifications to Spreadsheet component.
 */
public class KernelTabularDataAdapter implements UpdateLocationView, TabularData {
	private final Kernel kernel;
	private Map<Integer, Map<Integer, Object>> data = new HashMap<>();
	private List<TabularDataChangeListener> changeListeners = new ArrayList<>();

	public KernelTabularDataAdapter(Kernel kernel) {
		this.kernel = kernel;
	}

	@Override
	public void updateLocation(GeoElement geo) {

	}

	@Override
	public void add(GeoElement geo) {
		update(geo);
	}

	@Override
	public void remove(GeoElement geo) {

	}

	@Override
	public void rename(GeoElement geo) {

	}

	@Override
	public void update(GeoElement geo) {
		GPoint pt = GeoElementSpreadsheet.spreadsheetIndices(geo.getLabelSimple());
		if (pt != null) {
			setContent(pt.y, pt.x, geo);
		}
		changeListeners.forEach(listener -> listener.update(pt.y, pt.x));
	}

	@Override
	public void updateVisualStyle(GeoElement geo, GProperty prop) {

	}

	@Override
	public void updateHighlight(GeoElementND geo) {

	}

	@Override
	public void updateAuxiliaryObject(GeoElement geo) {

	}

	@Override
	public void repaintView() {

	}

	@Override
	public boolean suggestRepaint() {
		return false;
	}

	@Override
	public void reset() {

	}

	@Override
	public void clearView() {

	}

	@Override
	public void setMode(int mode, ModeSetter m) {

	}

	@Override
	public int getViewID() {
		return 0;
	}

	@Override
	public boolean hasFocus() {
		return false;
	}

	@Override
	public void startBatchUpdate() {

	}

	@Override
	public void endBatchUpdate() {

	}

	@Override
	public void updatePreviewFromInputBar(GeoElement[] geos) {

	}

	@Override
	public void reset(int rows, int columns) {

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

	}

	@Override
	public void insertRowAt(int row) {

	}

	@Override
	public void deleteRowAt(int row) {

	}

	@Override
	public void appendColumns(int columns) {

	}

	@Override
	public void insertColumnAt(int column) {

	}

	@Override
	public void deleteColumnAt(int column) {

	}

	@Override
	public void setContent(int row, int column, Object content) {
		data.computeIfAbsent(row, ignore -> new HashMap<Integer, Object>()).put(column, content);
	}

	@Override
	public Object contentAt(int row, int column) {
		return data.get(row) != null ? data.get(row).get(column) : null;
	}

	public void addChangeListener(TabularDataChangeListener changeListener) {
		changeListeners.add(changeListener);
	}
}
