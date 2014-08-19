package geogebra.common.main;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.statistics.AlgoCellRange;

import java.util.ArrayList;

/**
 * Maintains a list of all instances of AlgoCellRange in a construction and
 * handles updates to the AlgoCellRanges when a spreadsheet cell GeoElement is
 * changed.
 * 
 * @author G. Sturr
 * 
 */
public class AlgoCellRangeManager {

	private ArrayList<AlgoCellRange> cellRangeAlgos;

	/**
	 * Adds an AlgoCellRange algorithm to the internal algorithm list.
	 * 
	 * @param algo
	 *            algorithm to add
	 */
	public void registerCellRangeListenerAlgo(AlgoCellRange algo) {
		if (cellRangeAlgos == null) {
			cellRangeAlgos = new ArrayList<AlgoCellRange>();
		}

		if (!cellRangeAlgos.contains(algo)) {
			cellRangeAlgos.add(algo);
		}
	}

	/**
	 * Removes an AlgoCellRange algorithm from the internal algorithm list.
	 * 
	 * @param algo
	 *            algorithm to remove
	 */
	public void unregisterCellRangeListenerAlgo(AlgoCellRange algo) {
		if (cellRangeAlgos != null) {
			cellRangeAlgos.remove(algo);
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
	public void updateCellRangeAlgos(GeoElement geo, GPoint location,
			boolean isRemoveAction) {

		if (geo == null || cellRangeAlgos == null) {
			return;
		}

		for (AlgoCellRange algo : cellRangeAlgos) {
			// System.out.println("geo label: " + geo.getLabelSimple());
			if (algo.getCellRange().contains(location)) {
				algo.updateList(geo, isRemoveAction);
			}
		}

	}
	
	
	public void removeAll(){
		cellRangeAlgos.clear();
	}

}
