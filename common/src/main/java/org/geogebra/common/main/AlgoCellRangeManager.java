package org.geogebra.common.main;

import java.util.HashMap;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.statistics.AlgoCellRange;
import org.geogebra.common.spreadsheet.core.SpreadsheetCoords;

/**
 * Maintains a list of all instances of AlgoCellRange in a construction and
 * handles updates to the AlgoCellRanges when a spreadsheet cell GeoElement is
 * changed.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoCellRangeManager {
	private HashMap<String, AlgoCellRange> algos;

	/**
	 * Removes an AlgoCellRange algorithm from the internal algorithm list.
	 * 
	 * @param algo
	 *            algorithm to remove
	 */
	public void unregisterCellRangeListenerAlgo(AlgoCellRange algo) {
		if (algos != null) {
			algos.remove(getKey(algo.getStart(), algo.getEnd()));
		}

	}

	/**
	 * Updates registered AlgoCellRanges with cell ranges that contain the
	 * coordinates of the given GeoElement. This method should be called when
	 * GeoElements have been added, removed or renamed.
	 * 
	 * @param geo
	 *            GeoElement that has changed
	 * @param location
	 *            spreadsheet coordinate location (this may be the previous
	 *            location if a cell has been renamed)
	 * @param isRemoveAction
	 *            true if the given GeoElement has been removed
	 * 
	 */
	public void updateCellRangeAlgos(GeoElement geo, SpreadsheetCoords location,
			boolean isRemoveAction) {
		if (geo == null || algos == null) {
			return;
		}

		for (AlgoCellRange algo : algos.values()) {
			if (algo.getRange().contains(location)) {
				algo.updateList(geo, isRemoveAction);
			}
		}
	}

	/**
	 * add the geo at specified location to cell range algos
	 * 
	 * @param geo
	 *            geo
	 * @param location
	 *            location on spreadsheet
	 */
	public void addToCellRangeAlgos(GeoElement geo, SpreadsheetCoords location) {
		if (geo == null || algos == null) {
			return;
		}

		for (AlgoCellRange algo : algos.values()) {
			if (algo.getRange().contains(location)) {
				algo.addToList(geo, location);
			}
		}
	}

	/**
	 * CLear the algo list
	 */
	public void clear() {
		if (algos != null) {
			algos.clear();
		}
	}

	/**
	 * 
	 * @param cons
	 *            construction
	 * @param label
	 *            label
	 * @param start
	 *            start cell
	 * @param end
	 *            end cell
	 * @return algo corresponding to string start:end
	 */
	public AlgoCellRange getAlgoCellRange(Construction cons, String label,
			String start, String end) {

		if (algos == null) {
			algos = new HashMap<>();
		}

		String key = getKey(start, end);
		AlgoCellRange algo = algos.get(key);
		if (algo == null) {
			algo = new AlgoCellRange(cons, label, start, end);
			algos.put(key, algo);
		} else {
			if (label != null && label.length() > 0) {
				algo.getList().setLabel(label);
			}
			if (!algo.isInConstructionList()) {
				cons.addToConstructionList(algo, false);
			}
		}

		return algo;

	}

	private static String getKey(String start, String end) {
		return start + ":" + end;
	}

}
