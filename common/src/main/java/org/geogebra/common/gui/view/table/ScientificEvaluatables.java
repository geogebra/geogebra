package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;

public class ScientificEvaluatables {

	private Construction construction;

	private GeoFunction functionF;
	private GeoFunction functionG;

	/**
	 * @param construction {@link Construction}
	 */
	public ScientificEvaluatables(Construction construction) {
		this.construction = construction;
		functionF = createFunction(construction, "f");
		functionG = createFunction(construction, "g");
	}

	/**
	 * @return The current definition of function f in the construction.
	 */
	public GeoFunction getFunctionF() {
		GeoElement element = construction.lookupLabel("f");
		if (element instanceof GeoFunction) {
			return (GeoFunction) element;
		}
		return null;
	}

	/**
	 * @return The current definition of function g in the construction.
	 */
	public GeoFunction getFunctionG() {
		GeoElement element = construction.lookupLabel("g");
		if (element instanceof GeoFunction) {
			return (GeoFunction) element;
		}
		return null;
	}

	private GeoFunction createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.setAuxiliaryObject(true);
		function.rename(label);
		return function;
	}

	/**
	 * Add evaluatables to the table of values.
	 *
	 * @param table to add to
	 */
	public void addToTableOfValues(TableValues table) {
		table.addAndShow((GeoElement) functionF);
		table.addAndShow((GeoElement) functionG);
		try {
			table.setValues(-2, 2, 1);
		} catch (InvalidValuesException e) {
			throw new RuntimeException(e);
		} finally {
			ensureOnlyOneUndoInfo();
		}
	}

	private void ensureOnlyOneUndoInfo() {
		construction.getUndoManager().clearUndoInfo();
		construction.storeUndoInfo();
	}
}
