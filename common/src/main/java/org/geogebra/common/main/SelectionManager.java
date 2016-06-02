package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.geos.GProperty;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.GeoClass;

/**
 * Keeps lists of selected geos (global, per type)
 *
 */
public class SelectionManager {
	/** list of selected geos */
	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<GeoElement>();

	private final Kernel kernel;

	private final UpdateSelection listener;

	private ArrayList<GeoElementSelectionListener> selectionListeners;

	private final ArrayList<GeoPointND> selectedPoints = new ArrayList<GeoPointND>();
	private final ArrayList<GeoNumeric> selectedNumbers = new ArrayList<GeoNumeric>();
	private final ArrayList<GeoNumberValue> selectedNumberValues = new ArrayList<GeoNumberValue>();
	private final ArrayList<GeoLineND> selectedLines = new ArrayList<GeoLineND>();
	private final ArrayList<GeoDirectionND> selectedDirections = new ArrayList<GeoDirectionND>();
	private final ArrayList<GeoSegmentND> selectedSegments = new ArrayList<GeoSegmentND>();
	private final ArrayList<Region> selectedRegions = new ArrayList<Region>();
	private final ArrayList<Path> selectedPaths = new ArrayList<Path>();
	private final ArrayList<GeoConicND> selectedConicsND = new ArrayList<GeoConicND>();
	private final ArrayList<GeoImplicit> selectedImplicitpoly = new ArrayList<GeoImplicit>();
	private final ArrayList<GeoImplicitSurfaceND> selectedImplicitSurface = new ArrayList<GeoImplicitSurfaceND>();
	private final ArrayList<GeoFunction> selectedFunctions = new ArrayList<GeoFunction>();
	private final ArrayList<GeoFunctionNVar> selectedFunctionsNVar = new ArrayList<GeoFunctionNVar>();
	private final ArrayList<GeoCurveCartesian> selectedCurves = new ArrayList<GeoCurveCartesian>();
	private final ArrayList<GeoVectorND> selectedVectors = new ArrayList<GeoVectorND>();
	private final ArrayList<GeoPolygon> selectedPolygons = new ArrayList<GeoPolygon>();
	private final ArrayList<GeoPolyLine> selectedPolyLines = new ArrayList<GeoPolyLine>();
	private final ArrayList<GeoElement> selectedGeosEuclidian = new ArrayList<GeoElement>();
	private final ArrayList<GeoList> selectedLists = new ArrayList<GeoList>();

	private boolean geoToggled = false;

	/**
	 * @param kernel
	 *            kernel
	 * @param listener
	 *            listener to be notified on selection updates
	 */
	public SelectionManager(Kernel kernel, UpdateSelection listener) {
		this.kernel = kernel;
		this.listener = listener;

		selectionListeners = new ArrayList<GeoElementSelectionListener>();
	}

	/**
	 * Clears selection and selects given geos.
	 * 
	 * @param geos
	 *            geos
	 */
	final public void setSelectedGeos(ArrayList<GeoElement> geos) {
		setSelectedGeos(geos, true);
	}

	/**
	 * Clears selection and selects given geos.
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

		// notify all registered selection listeners
		for (GeoElementSelectionListener sl : getSelectionListeners()) {
			if (sl != null) {
				sl.geoElementSelected(geo, true);
			}
		}

	}

	private void setGeoToggled(boolean flag) {
		geoToggled = flag;
	}

	/**
	 * Resets the flag for selection change, see {@link #isGeoToggled()}
	 */
	public void resetGeoToggled() {
		setGeoToggled(false);
	}

	/**
	 * @return the flag for selection change since last object creation (using
	 *         tool)
	 */
	public boolean isGeoToggled() {
		return geoToggled;
	}

	/**
	 * Michael Borcherds 2008-03-03
	 * 
	 * @return -1 if nothing selected return -2 if objects from more than one
	 *         layer selected return layer number if objects from exactly one
	 *         layer are selected
	 */
	public int getSelectedLayer() {
		if (getSelectedGeos().size() == 0)
			return -1; // return -1 if nothing selected

		int layer = getSelectedGeos().get(0).getLayer();

		for (int i = 1; i < getSelectedGeos().size(); i++) {
			GeoElement geo = getSelectedGeos().get(i);
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
	 * @return whether one or more of selected geos have predecessors
	 */
	final public boolean hasPredecessors() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllPredecessors();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				geo = it2.next();
				if (geo.isLabelSet()) {
					return true;
				}
			}
		}
		return false;
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
	 * @return whether one or more of selected geos have descendants
	 */
	final public boolean hasDescendants() {

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			TreeSet<GeoElement> tree = geo.getAllChildren();
			Iterator<GeoElement> it2 = tree.iterator();
			while (it2.hasNext()) {
				geo = it2.next();
				if (geo.isLabelSet()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Invert visibility of all selected objects
	 */
	final public void showHideSelection() {

		// GeoElements may have other GeoElements as subelements,
		// and this means that the subelements should be tackled first,
		// in order to prevent them being tackled twice, and this way
		// negating the negative, doing nothing and other complications

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			if (!geo.isGeoPolygon() && !geo.isGeoPolyhedron()
					&& !geo.isGeoPolyLine()
					&& geo.getGeoClassType() != GeoClass.QUADRIC_LIMITED
					&& geo.getGeoClassType() != GeoClass.NET) {
				geo.setEuclidianVisible(!geo.isEuclidianVisible());
				geo.updateVisualStyle(GProperty.VISIBLE);
			}
		}

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			if (geo.isGeoPolygon() || geo.isGeoPolyLine()
					|| geo.getGeoClassType() == GeoClass.QUADRIC_LIMITED) {
				geo.setEuclidianVisible(!geo.isEuclidianVisible());
				geo.updateVisualStyle(GProperty.VISIBLE);
			}
		}

		for (int i = 0; i < selectedGeos.size(); i++) {
			GeoElement geo = selectedGeos.get(i);
			if (geo.isGeoPolyhedron() || geo.getGeoClassType() == GeoClass.NET) {
				geo.setEuclidianVisible(!geo.isEuclidianVisible());
				geo.updateVisualStyle(GProperty.VISIBLE);
			}
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
			geo.updateVisualStyle(GProperty.LABEL_STYLE);
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

		setGeoToggled(true);

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
	 * 
	 * @param ev
	 *            view that should get focus after (if we did not selct
	 *            textfield)
	 * @param cycle
	 *            whether to jump back to 0 from the last one
	 * 
	 * @return whether the operation is successful (e.g. in case of no cycle)
	 */
	final public boolean selectNextGeo(EuclidianViewInterfaceCommon ev,
			boolean cycle) {

		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();

		tree = new TreeSet<GeoElement>(tree);

		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);

		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		// or are not visible in the view
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed(ev) || !geo.isEuclidianVisible()
					|| !geo.isVisibleInView(ev.getViewID())) {
				tree.remove(geo);
			}
		}

		it = tree.iterator();

		// none selected, select first geo
		if (selectedGeos.size() == 0) {
			if (it.hasNext()) {
				addSelectedGeo(it.next());
			}
			return false;
		}

		GeoElement selGeo = null;

		if (selectedGeos.size() != 1) {
			if (cycle) {
				return false;
			}

			GeoElement actual = null;

			// in case of no cycle, it also means that this
			// was called from Web, in Graphics views, so
			// in this case, it's better to change selection
			// to remove all geos except the first one that
			// is also part of this view, and continue
			// as if it were always the case...
			Iterator<GeoElement> itt = tree.iterator();
			while (itt.hasNext()) {
				actual = itt.next();
				if (selectedGeos.contains(actual)) {// && (selGeo == null) // redundant
					selGeo = actual;
					break;
				}
			}

			if (selGeo == null) {
				// no selected geo in this view,
				// maybe better to handle this as if
				// there were no geo selected!
				// but also clear selected geos!
				itt = selectedGeos.iterator();
				while (itt.hasNext()) {
					// does something more than simple clear
					removeSelectedGeo(itt.next(), false, true);
				}

				if (it.hasNext()) {
					addSelectedGeo(it.next());
				}
				return false;

				// old behaviour
				// selGeo = selectedGeos.get(0);
			}

			// remove every GeoElement from the selection
			// that is not "selGeo"
			itt = selectedGeos.iterator();
			while (itt.hasNext()) {
				// does something more than simple clear
				removeSelectedGeo(itt.next(), false, true);
			}
			// and put selGeo back
			addSelectedGeo(selGeo);

			// make sure it is fresh, for a bug when points
			// A and C are selected, it gone to B and C
			it = tree.iterator();
		}

		if (selGeo == null) {
			// at least one selected, select next one
			selGeo = selectedGeos.get(0);
		}

		// maybe selGeo is there in Graphics View 2,
		// but it is not there in "tree", since we're
		// in Graphics View 1! Then we probably want
		// the same thing as when nothing is selected here!
		if (!tree.contains(selGeo)) {
			// but only after clearing the selection properly!
			Iterator<GeoElement> itt = selectedGeos.iterator();
			while (itt.hasNext()) {
				// does something more than simple clear
				removeSelectedGeo(itt.next(), false, true);
			}

			if (it.hasNext()) {
				addSelectedGeo(it.next());
			}
			return false;
		}

		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (selGeo == geo) {
				removeSelectedGeo(selGeo);
				if (!it.hasNext()) {
					if (cycle) {
						it = tree.iterator();
					} else {
						return false;
					}
				}
				GeoElement next = it.next();
				addSelectedGeo(next);

				// make sure Input Boxes lose focus on <TAB>
				if (!(next instanceof GeoInputBox)) {
					ev.requestFocus();
				}
				break;
			}
		}
		return true;
	}

	/**
	 * Select last created geo
	 * 
	 * @param ev
	 *            view that should get focus after (if we did not selct
	 *            textfield)
	 */
	final public void selectLastGeo(EuclidianViewInterfaceCommon ev) {
		if (selectedGeos.size() != 1) {
			return;
		}
		GeoElement selGeo = selectedGeos.get(0);
		GeoElement lastGeo = null;
		TreeSet<GeoElement> tree = kernel.getConstruction()
				.getGeoSetLabelOrder();

		tree = new TreeSet<GeoElement>(tree);

		TreeSet<GeoElement> copy = new TreeSet<GeoElement>(tree);
		Iterator<GeoElement> it = copy.iterator();

		// remove geos that don't have isSelectionAllowed()==true
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (!geo.isSelectionAllowed(ev) || !geo.isEuclidianVisible()
					|| !geo.isVisibleInView(ev.getViewID())) {
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
				if (!(lastGeo instanceof GeoInputBox)) {
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

		updateSelection(true);
	}

	/**
	 * Update stylebars, menubar and properties view to match selection
	 * 
	 * @param updatePropertiesView
	 *            whether to update properties view
	 */
	public void updateSelection(boolean updatePropertiesView) {
		listener.updateSelection(updatePropertiesView);
	}

	/**
	 * Add a selection listener
	 * 
	 * @param sl
	 *            GeoElementSelectionListener to be added
	 */
	public void addSelectionListener(GeoElementSelectionListener sl) {
		selectionListeners.add(sl);
	}

	/**
	 * Remove a selection listener
	 * 
	 * @param sl
	 *            GeoElementSelectionListener to be removed
	 */
	public void removeSelectionListener(GeoElementSelectionListener sl) {
		selectionListeners.remove(sl);
	}

	/**
	 * @return Set of all registered SelectionListeners
	 */
	public ArrayList<GeoElementSelectionListener> getSelectionListeners() {
		return selectionListeners;
	}

	public ArrayList<GeoPointND> getSelectedPointList() {
		return selectedPoints;
	}

	public ArrayList<GeoNumeric> getSelectedNumberList() {
		return selectedNumbers;
	}

	public ArrayList<GeoNumberValue> getSelectedNumberValueList() {
		return selectedNumberValues;
	}

	public ArrayList<GeoLineND> getSelectedLineList() {
		return selectedLines;
	}

	public ArrayList<Path> getSelectedPathList() {
		return selectedPaths;
	}

	public ArrayList<GeoConicND> getSelectedConicNDList() {
		return selectedConicsND;
	}

	public ArrayList<GeoDirectionND> getSelectedDirectionList() {
		return selectedDirections;
	}

	public ArrayList<GeoSegmentND> getSelectedSegmentList() {
		return selectedSegments;
	}

	public ArrayList<Region> getSelectedRegionList() {
		return selectedRegions;
	}

	public ArrayList<GeoImplicit> getSelectedImplicitpolyList() {
		return selectedImplicitpoly;
	}

	public ArrayList<GeoImplicitSurfaceND> getSelectedImplicitSurfaceList() {
		return selectedImplicitSurface;
	}

	public ArrayList<GeoFunction> getSelectedFunctionList() {
		return selectedFunctions;
	}

	public ArrayList<GeoFunctionNVar> getSelectedFunctionNVarList() {
		return selectedFunctionsNVar;
	}

	public ArrayList<GeoCurveCartesian> getSelectedCurveList() {
		return selectedCurves;
	}

	public ArrayList<GeoVectorND> getSelectedVectorList() {
		return selectedVectors;
	}

	public ArrayList<GeoPolygon> getSelectedPolygonList() {
		return selectedPolygons;
	}

	public ArrayList<GeoPolyLine> getSelectedPolyLineList() {
		return selectedPolyLines;
	}

	public ArrayList<GeoElement> getSelectedGeoList() {
		return selectedGeosEuclidian;
	}

	public ArrayList<GeoList> getSelectedListList() {
		return selectedLists;
	}

	public <T> int addToSelectionList(ArrayList<T> selectionList,
			T geo, int max) {
		if (geo == null) {
			return 0;
		}

		int ret = 0;
		if (selectionList.contains(geo)) { // remove from selection
			selectionList.remove(geo);
			if (!selectionList.equals(getSelectedGeoList())) {
				getSelectedGeoList().remove(geo);
			}
			removeSelectedGeo((GeoElement) geo, true, true);
			ret = -1;
		} else { // new element: add to selection
			if (selectionList.size() < max) {
				selectionList.add(geo);
				if (!selectionList.equals(getSelectedGeoList())) {
					getSelectedGeoList().add((GeoElement) geo);
				}
				addSelectedGeo((GeoElement) geo, true, true);
				ret = 1;
			}
		}

		if (ret != 0) {
			setGeoToggled(true);
		}

		return ret;
	}
}
