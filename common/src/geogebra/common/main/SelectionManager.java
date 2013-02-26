package geogebra.common.main;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoTextField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

public class SelectionManager {
	/** list of selected geos */
	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();
	
	private final Kernel kernel;
	
	private final UpdateSelection listener;
	
	public SelectionManager(Kernel kernel,UpdateSelection app){
		this.kernel = kernel;
		this.listener = app;
	}
	
	/**
	 * Clears selction and selects given geos.
	 * 
	 * @param geos
	 *            geos
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos) {
		setSelectedGeos(geos, true);
	}

	/**
	 * Clears selction and selects given geos.
	 * 
	 * @param geos
	 *            geos
	 * @param updateSelection
	 *            says if selection has to be updated
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos,
			boolean updateSelection) {
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		if (updateSelection)
			updateSelection();
	}
	
	/**
	 * Clears selection and repaints all views
	 */
	final public void clearSelectedGeos() {
		clearSelectedGeos(true);
	}

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 */
	public void clearSelectedGeos(boolean repaint) {

		clearSelectedGeos(repaint, repaint);
	}

	/**
	 * Clear selection
	 * 
	 * @param repaint
	 *            whether all views need repainting afterwards
	 * @param updateSelection
	 *            call (or not) updateSelection()
	 */
	public void clearSelectedGeos(boolean repaint, boolean updateSelection) {
		int size = selectedGeos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				GeoElement geo = selectedGeos.get(i);
				geo.setSelected(false);
			}
			selectedGeos.clear();
			if (repaint)
				kernel.notifyRepaint();

			if (updateSelection)
				updateSelection();

		}

	}
	
	/**
	 * Removes geo from selection
	 * 
	 * @param geo
	 *            geo to be removed
	 * @param repaint
	 *            whether views must be repainted after
	 * @param updateSelection
	 *            whether update selection needs to be done after
	 */
	final public void removeSelectedGeo(GeoElement geo, boolean repaint,
			boolean updateSelection) {
		if (geo == null) {
			return;
		}

		if (selectedGeos.remove(geo)) {
			// update only if selectedGeos contained geo
			geo.setSelected(false);
			if (updateSelection)
				updateSelection();
			if (repaint) {
				kernel.notifyRepaint();
			}

		}
	}
	
	/**
	 * @return list of selected geos
	 */
	public final ArrayList<GeoElement> getSelectedGeos() {
		return selectedGeos;
	}
	
	/**
	 * Adds geo to selection
	 * 
	 * @param geo
	 *            geo to be added to selection
	 * @param repaint
	 *            whether repaint is needed
	 * @param updateSelection
	 *            whether selection update is needed
	 */
	public final void addSelectedGeo(GeoElement geo, boolean repaint,
			boolean updateSelection) {
		if ((geo == null) || selectedGeos.contains(geo)) {
			return;
		}

		selectedGeos.add(geo);
		geo.setSelected(true);
		if (repaint) {
			kernel.notifyRepaint();
		}

		if (updateSelection)
			updateSelection();

	}
	
	/**
	 * Michael Borcherds 2008-03-03
	 * 
	 * @return -1 if nothing selected return -2 if objects from more than one
	 *         layer selected return layer number if objects from exactly one
	 *         layer are selected
	 */
	public int getSelectedLayer() {
		Object[] geos = getSelectedGeos().toArray();
		if (geos.length == 0)
			return -1; // return -1 if nothing selected

		int layer = ((GeoElement) geos[0]).getLayer();

		for (int i = 1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer)
				return -2; // return -2 if more than one layer selected
		}
		return layer;
	}

	/**
	 * Selects all geos in given layer
	 * 
	 * @param layer
	 *            0 - 9 for particular layer, -1 for all layers (Michael
	 *            Borcherds, 2008-03-03)
	 */
	final public void selectAll(int layer) {
		clearSelectedGeos(false);

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if ((layer == -1) || (geo.getLayer() == layer)) {
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Select objects that were not selected so far and vice versa.
	 */
	final public void invertSelection() {

		Iterator<GeoElement> it = kernel.getConstruction()
				.getGeoSetLabelOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selectedGeos.contains(geo)) {
				removeSelectedGeo(geo, false, false);
			} else {
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Select all predecessors of all selected geos
	 */
	final public void selectAllPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllPredecessors();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				geo = it2.next();
				if (geo.isLabelSet()) {
					addSelectedGeo(geo, false, false);
				}
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	/**
	 * Selects descendants of all visible objects
	 */
	final public void selectAllDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllChildren();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				geo = it2.next();
				if (geo.isLabelSet()) {
					addSelectedGeo(geo, false, false);
				}
			}
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Invert visibility of all selected objects
	 */
	final public void showHideSelection() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setEuclidianVisible(!geo.isEuclidianVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}

	/**
	 * Invert visibility of labels of all selected objects
	 */
	final public void showHideSelectionLabels() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			geo.setLabelVisible(!geo.isLabelVisible());
			geo.updateVisualStyle();
		}
		kernel.notifyRepaint();
		updateSelection();
	}
	
	/**
	 * @param geo
	 *            geo
	 * @return whether given geo belongs to selection
	 */
	final public boolean containsSelectedGeo(GeoElement geo) {
		return selectedGeos.contains(geo);
	}

	/**
	 * @param geos
	 *            geos
	 * @return whether given geos belongs to selection
	 */
	final public boolean containsSelectedGeos(ArrayList<GeoElement> geos) {
		return selectedGeos.containsAll(geos);
	}

	/**
	 * Removes geo from selection
	 * 
	 * @param geo
	 *            geo to be removed
	 */
	final public void removeSelectedGeo(GeoElement geo) {
		removeSelectedGeo(geo, true, true);
	}
	
	/**
	 * @return number of selected geos
	 */
	public final int selectedGeosSize() {
		return selectedGeos.size();
	}
	
	/**
	 * Selects the first geo in the construction. Previous selected geos are
	 * unselected (used e.g. for xAxis).
	 * 
	 * @return first geo or null
	 */
	final public GeoElement setFirstGeoSelectedForPropertiesView() {
		GeoElement geo = kernel.getFirstGeo();
		if (geo == null)
			return null;

		clearSelectedGeos(false);
		selectedGeos.add(geo);
		geo.setSelected(true);
		kernel.notifyRepaint();

		listener.updateSelection(false);

		return geo;

	}
	
	/**
	 * Adds given geo to selection
	 * 
	 * @param geo
	 *            geo
	 */
	public final void addSelectedGeo(GeoElement geo) {
		addSelectedGeo(geo, true, true);
	}

	

	/**
	 * Adds geos to selection
	 * 
	 * @param geos
	 *            geos to be added to selection
	 * @param repaint
	 *            whether repaint is needed
	 */
	public final void addSelectedGeos(ArrayList<GeoElement> geos,
			boolean repaint) {

		selectedGeos.addAll(geos);
		for (int i = 0; i < geos.size(); i++) {
			geos.get(i).setSelected(true);
		}
		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}
	
	/**
	 * Removes or adds given geo to selection
	 * 
	 * @param geo
	 *            geo to be added / removed
	 * @param repaint
	 *            whether we want to repaint afterwards
	 */
	final public void toggleSelectedGeo(GeoElement geo, boolean repaint) {
		if (geo == null) {
			return;
		}

		boolean contains = selectedGeos.contains(geo);
		if (contains) {
			selectedGeos.remove(geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			geo.setSelected(true);
		}

		if (repaint) {
			kernel.notifyRepaint();
		}
		updateSelection();
	}
	
	/**
	 * Removes or adds given geo to selection and repaints views
	 * 
	 * @param geo
	 *            geo to be added / removed
	 */
	final public void toggleSelectedGeo(GeoElement geo) {
		toggleSelectedGeo(geo, true);
	}
	
	/**
	 * Select geo next to the selected one in construction order. If none is
	 * selected before, first geo is selected.
	 * @param ev view that should get focus after (if we did not selct textfield)
	 */
	final public void selectNextGeo(EuclidianViewInterfaceCommon ev) {

		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();

		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);

		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();

		// none selected, select first geo
		if (selectedGeos.size() == 0) {
			if (it.hasNext()) {
				addSelectedGeo(it.next());
			}
			return;
		}

		if (selectedGeos.size() != 1) {
			return;
		}

		// one selected, select next one
		GeoElement selGeo = selectedGeos.get(0);
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				if (!it.hasNext()) {
					it = tree.iterator();
				}
				GeoElement next = it.next();
				addSelectedGeo(next);

				// make sure Input Boxes lose focus on <TAB>
				if (!(next instanceof GeoTextField)) {
					ev.requestFocus();
				}
				break;
			}
		}
	}

	/**
	 * Select last created geo
	 * @param ev view that should get focus after (if we did not selct textfield)
	 */
	final public void selectLastGeo(EuclidianViewInterfaceCommon ev) {
		if (selectedGeos.size() != 1) {
			return;
		}
		GeoElement selGeo = selectedGeos.get(0);
		GeoElement lastGeo = null;
		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();
		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);
		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed()) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();
		while (it.hasNext()) {
			lastGeo = it.next();
		}

		it = tree.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				addSelectedGeo(lastGeo);

				// make sure Input Boxes lose focus on <SHIFT><TAB>
				if (!(lastGeo instanceof GeoTextField)) {
					ev.requestFocus();
				}

				break;
			}
			lastGeo = geo;
		}
	}
	
	/**
	 * Update stylebars, menubar and properties view to match selection
	 */
	public void updateSelection() {
		listener.updateSelection(true);
	}

}
