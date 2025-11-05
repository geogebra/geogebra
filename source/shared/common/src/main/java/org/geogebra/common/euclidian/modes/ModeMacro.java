package org.geogebra.common.euclidian.modes;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.TestGeo;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.SelectionManager;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.editor.share.util.Unicode;

/**
 * Macro mode handler.
 */
public class ModeMacro {
	protected Macro macro;
	protected TestGeo[] macroInput;
	private int index;
	private EuclidianController ec;
	private Kernel kernel;
	private Localization localization;
	private SelectionManager selection;

	/**
	 * @param ec
	 *            controller
	 */
	public ModeMacro(EuclidianController ec) {
		this.ec = ec;
		this.kernel = ec.getKernel();
		this.localization = kernel.getLocalization();
		this.selection = kernel.getApplication().getSelectionManager();
	}

	/**
	 * If enough inputs are selected, process macro.
	 * 
	 * @param callback2
	 *            callback
	 * @return whether macro was processed
	 */
	public boolean macroProcess(AsyncOperation<Boolean> callback2) {
		// do we have everything we need?
		if (ec.selGeos() == macroInput.length) {
			GeoElement[] res = kernel.useMacro(null, macro, ec.getSelectedGeos());
			if (callback2 != null) {
				callback2.callback(true);
			}
			return res != null;
		}
		if (callback2 != null) {
			callback2.callback(false);
		}
		return false;
	}

	/**
	 * Handles selected objects for a macro
	 *
	 * @param hits0
	 *            hits
	 * @param callback2
	 *            callback when number input is needed
	 * @param selPreview
	 *            whether this is for preview
	 * @return whether macro was successfully processed
	 */
	public final boolean macro(Hits hits0, final AsyncOperation<Boolean> callback2,
			boolean selPreview) {
		// try to get next needed type of macroInput
		index = ec.selGeos();
		Hits hits = hits0;
		// we want a polyhedron, maybe we hit its side?
		if (macroInput[index] == TestGeo.GEOPOLYHEDRON) {
			hits = hits.getPolyhedronsIncludingMetaHits();
		}
		// standard case: try to get one object of needed input type
		boolean objectFound = 1 == ec.handleAddSelected(hits, macroInput.length, false,
				selection.getSelectedGeoList(), macroInput[index], selPreview);

		// we're done if in selection preview
		if (selPreview) {
			if (callback2 != null) {
				callback2.callback(false);
			}
			return false;
		}

		// only one point needed: try to create it
		if (!objectFound && (macroInput[index].equals(TestGeo.GEOPOINT)
				|| macroInput[index].equals(TestGeo.GEOPOINTND))) {
			GeoPointND newPoint = ec.createNewPoint(hits, true, false, true, false, false);
			if (newPoint != null) {
				// take movedGeoPoint which is the newly created point
				selection.getSelectedGeoList().add(newPoint.toGeoElement());
				selection.addSelectedGeo(newPoint);
				objectFound = true;
				ec.resetPointCreated();
			}
		}

		// object found in handleAddSelected()
		if (objectFound || macroInput[index].equals(TestGeo.GEONUMERIC)
				|| macroInput[index].equals(TestGeo.GEOANGLE)) {
			if (!objectFound) {
				index--;
			}

			AsyncOperation<GeoNumberValue> callback3 = new AsyncOperation<>() {

				@Override
				public void callback(GeoNumberValue num) {
					handleNumber(num, callback2, this);
				}

			};
			// look ahead if we need a number or an angle next
			readNumberOrAngleIfNeeded(callback3);
		}

		return macroProcess(callback2);
	}

	protected void handleNumber(GeoNumberValue num, AsyncOperation<Boolean> callback2,
			AsyncOperation<GeoNumberValue> callback3) {
		if (num == null) {
			// no success: reset mode
			ec.getView().resetMode();
			if (callback2 != null) {
				callback2.callback(false);
			}
			return;
		}
		// great, we got our number
		if (num.isGeoElement()) {
			selection.getSelectedGeoList().add(num.toGeoElement());
		}

		readNumberOrAngleIfNeeded(callback3);

		if (ec.selGeos() == macroInput.length) {
			if (macroProcess(callback2)) {
				ec.storeUndoInfo();
			}
		}

	}

	/**
	 * Wait for number or angle for a macro.
	 * 
	 * @param callback3
	 *            callback
	 */
	public void readNumberOrAngleIfNeeded(AsyncOperation<GeoNumberValue> callback3) {
		if (++index < macroInput.length) {

			// maybe we need a number
			if (macroInput[index].equals(TestGeo.GEONUMERIC)) {
				kernel.getApplication().getDialogManager().showNumberInputDialog(
						macro.getToolOrCommandName(),
						localization.getMenu("Numeric"), null, callback3);

			}

			// maybe we need an angle
			else if (macroInput[index].equals(TestGeo.GEOANGLE)) {
				kernel.getApplication().getDialogManager().showAngleInputDialog(
						macro.getToolOrCommandName(),
						localization.getMenu("Angle"), Unicode.FORTY_FIVE_DEGREES_STRING,
						callback3);
			}
		}
	}

	/**
	 * Set macro mode.
	 * 
	 * @param mode1
	 *            app mode
	 */
	public void setMode(int mode1) {
		int macroID = mode1 - EuclidianConstants.MACRO_MODE_ID_OFFSET;
		macro = kernel.getMacro(macroID);
		macroInput = macro.getInputTypes();
	}
}
