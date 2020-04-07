package org.geogebra.test;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

public class UndoRedoTester {

	private App app;
	private Construction construction;


	public UndoRedoTester(App app) {
		this.app = app;
		construction = app.getKernel().getConstruction();
	}

	public void setupUndoRedo() {
		app.setUndoActive(true);
	}

	public  <T extends GeoElement> T getAfterUndo(String label) {
		construction.getUndoManager().undo();
		return (T) construction.lookupLabel(label);
	}

	public  <T extends GeoElement> T getAfterRedo(String label) {
		construction.getUndoManager().redo();
		return (T) construction.lookupLabel(label);
	}
}
