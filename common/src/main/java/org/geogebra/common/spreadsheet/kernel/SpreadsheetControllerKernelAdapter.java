package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.spreadsheet.core.Spreadsheet;

/**
 * Listens to changes of spreadsheet data (=GeoElements) in Kernel and passes
 * relevant notifications to Spreadsheet component.
 */
public class SpreadsheetControllerKernelAdapter implements UpdateLocationView {

	private final Spreadsheet spreadsheet;
	private final Kernel kernel;

	public SpreadsheetControllerKernelAdapter(Spreadsheet spreadsheet, Kernel kernel) {
		this.spreadsheet = spreadsheet;
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
			spreadsheet.setContent(pt.y, pt.x, geo);
		}
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
}
