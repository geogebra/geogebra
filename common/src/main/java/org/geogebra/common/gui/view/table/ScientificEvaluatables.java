package org.geogebra.common.gui.view.table;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoEvaluatable;
import org.geogebra.common.main.App;

public class ScientificEvaluatables {
	private GeoEvaluatable functionF;
	private GeoEvaluatable functionG;

	/**
	 * @param construction {@link Construction}
	 */
	public ScientificEvaluatables(Construction construction) {
		functionF = createFunction(construction, "f");
		functionG = createFunction(construction, "g");
	}

	private GeoEvaluatable createFunction(Construction construction, String label) {
		GeoFunction function = new GeoFunction(construction);
		function.rename(label);
		function.setAuxiliaryObject(true);
		return function;
	}

	/**
	 * Update the two evaluatables.
	 *
	 * @param functionF aka f(x).
	 * @param functionG aka g(x).
	 */
	public void update(GeoEvaluatable functionF, GeoEvaluatable functionG) {
		this.functionF = functionF;
		this.functionG = functionG;
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
			storeInitialUndoInfo();
		}
	}

	private void storeInitialUndoInfo() {
		App app = functionF.getApp();
		app.getUndoManager().clearUndoInfo();
		app.storeUndoInfo();
	}
}
