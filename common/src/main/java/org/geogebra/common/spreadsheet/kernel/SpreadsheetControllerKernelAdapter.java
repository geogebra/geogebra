package org.geogebra.common.spreadsheet.kernel;

import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.UpdateLocationView;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;

public class SpreadsheetControllerKernelAdapter implements UpdateLocationView {
	@Override
	public void updateLocation(GeoElement geo) {

	}

	@Override
	public void add(GeoElement geo) {

	}

	@Override
	public void remove(GeoElement geo) {

	}

	@Override
	public void rename(GeoElement geo) {

	}

	@Override
	public void update(GeoElement geo) {

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
