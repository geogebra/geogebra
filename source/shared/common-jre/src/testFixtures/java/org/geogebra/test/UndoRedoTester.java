package org.geogebra.test;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;

/**
 * Utility class for undo-redo testing
 */
public class UndoRedoTester {

	private App app;
	private Construction construction;

	/**
	 * @param app app
	 */
	public UndoRedoTester(App app) {
		this.app = app;
		construction = app.getKernel().getConstruction();
	}

	/**
	 * Prepares the app for undo-redo testing
	 */
	public void setupUndoRedo() {
		app.setUndoActive(true);
	}

	/**
	 * Executes undo and returns the element with the given label.
	 * @param label Label of the the GeoElement that we want to get
	 * @param <T> The exact type of the GeoElement
	 * @return the GeoElement with the given label
	 */
	public  <T extends GeoElement> T getAfterUndo(String label) {
		undo();
		return (T) construction.lookupLabel(label);
	}

	/**
	 * Executes undo
	 */
	public void undo() {
		construction.getUndoManager().undo();
	}

	/**
	 * Executes redo and returns the element with the given label.
	 * @param label Label of the the GeoElement that we want to get
	 * @param <T> The exact type of the GeoElement
	 * @return the GeoElement with the given label
	 */
	public  <T extends GeoElement> T getAfterRedo(String label) {
		redo();
		return (T) construction.lookupLabel(label);
	}

	/**
	 * Executes redo
	 */
	public void redo() {
		construction.getUndoManager().redo();
	}
}
