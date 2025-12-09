/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
