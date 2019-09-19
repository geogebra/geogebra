package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.gui.AccessibilityManagerInterface;
import org.geogebra.common.gui.view.algebra.AlgebraView.SortMode;
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
import org.geogebra.common.kernel.kernelND.GeoCoordSys;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoImplicitSurfaceND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoPolyhedronInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadric3DLimitedInterface;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.GeoClass;

/**
 * Keeps lists of selected geos (global, per type)
 *
 */
public class SelectionManager {
	/** list of selected geos */
	protected final ArrayList<GeoElement> selectedGeos = new ArrayList<>();

	private final Kernel kernel;

	private final ArrayList<UpdateSelection> listeners;

	private ArrayList<GeoElementSelectionListener> selectionListeners;

	private final ArrayList<GeoPointND> selectedPoints = new ArrayList<>();
	private final ArrayList<GeoNumeric> selectedNumbers = new ArrayList<>();
	private final ArrayList<GeoNumberValue> selectedNumberValues = new ArrayList<>();
	private final ArrayList<GeoLineND> selectedLines = new ArrayList<>();
	private final ArrayList<GeoDirectionND> selectedDirections = new ArrayList<>();
	private final ArrayList<GeoSegmentND> selectedSegments = new ArrayList<>();
	private final ArrayList<Region> selectedRegions = new ArrayList<>();
	private final ArrayList<Path> selectedPaths = new ArrayList<>();
	private final ArrayList<GeoConicND> selectedConicsND = new ArrayList<>();
	private final ArrayList<GeoImplicit> selectedImplicitpoly = new ArrayList<>();
	private final ArrayList<GeoImplicitSurfaceND> selectedImplicitSurface = new ArrayList<>();
	private final ArrayList<GeoFunction> selectedFunctions = new ArrayList<>();
	private final ArrayList<GeoFunctionNVar> selectedFunctionsNVar = new ArrayList<>();
	private final ArrayList<GeoCurveCartesian> selectedCurves = new ArrayList<>();
	private final ArrayList<GeoVectorND> selectedVectors = new ArrayList<>();
	private final ArrayList<GeoPolygon> selectedPolygons = new ArrayList<>();
	private final ArrayList<GeoPolyLine> selectedPolyLines = new ArrayList<>();
	private final ArrayList<GeoElement> selectedGeosEuclidian = new ArrayList<>();
	private final ArrayList<GeoList> selectedLists = new ArrayList<>();
	private final ArrayList<GeoCoordSys> selectedCS2D = new ArrayList<>();
	private final ArrayList<GeoQuadricND> selectedQuadric = new ArrayList<>();
	private final ArrayList<GeoQuadric3DLimitedInterface> selectedQuadricLim = new ArrayList<>();
	private final ArrayList<GeoPolyhedronInterface> selectedPolyhedron = new ArrayList<>();

	private ArrayList<GeoPlaneND> selectedPlane = new ArrayList<>();
	/** selected geos names just before undo/redo */
	private ArrayList<String> selectedGeosNames = new ArrayList<>();

	private boolean geoToggled = false;

	private ArrayList<GeoElement> tempMoveGeoList;

	/**
	 * @param kernel
	 *            kernel
	 * @param listener
	 *            listener to be notified on selection updates
	 */
	public SelectionManager(Kernel kernel, UpdateSelection listener) {
		this.kernel = kernel;
		this.listeners = new ArrayList<>(2);
		this.listeners.add(listener);

		selectionListeners = new ArrayList<>();
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
		// special case -- happens when we set the same selection on mouse down
		// and mouse up; we don't want too many events
		if (geos != null && geos.size() == 1 && selectedGeos.size() == 1
				&& geos.get(0) == selectedGeos.get(0)) {
			return;
		}
		clearSelectedGeos(false);
		if (geos != null) {
			for (int i = 0; i < geos.size(); i++) {
				GeoElement geo = geos.get(i);
				addSelectedGeo(geo, false, false);
			}
		}
		kernel.notifyRepaint();
		if (updateSelection) {
			updateSelection();
		}
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
				boolean oldSelected = geo.isSelected();
				geo.setSelected(false);
				if (geo.getKernel().getApplication()
						.isUnbundledOrWhiteboard()
						&& oldSelected) {

					notifyListeners(geo);

				}
			}
			selectedGeos.clear();
			if (repaint) {
				kernel.notifyRepaint();
			}

			if (updateSelection) {
				updateSelection();
			}

			kernel.getApplication().getEventDispatcher()
					.dispatchEvent(EventType.DESELECT, null);
		}

	}

	private void notifyListeners(GeoElement geo) {
		for (GeoElementSelectionListener sl : getSelectionListeners()) {
			if (sl != null) {
				sl.geoElementSelected(geo, false);
			}
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
		kernel.getApplication().getEventDispatcher()
				.dispatchEvent(EventType.DESELECT, geo);
		if (selectedGeos.remove(geo)) {
			// update only if selectedGeos contained geo
			geo.setSelected(false);
			if (updateSelection) {
				updateSelection();
			}
			if (repaint) {
				kernel.notifyRepaint();
			}

		}
	}

	/**
	 * Removes all geos from selection.
	 */
	final public void removeAllSelectedGeos() {
		if (selectedGeos.isEmpty()) {
			return;
		}

		for (GeoElement geo : selectedGeos) {
			// On desktop selectedGeos.remove(geo) here throws an exception,
			// so first iterate over, do stuff and then clear the array is the proper way.

			kernel.getApplication().getEventDispatcher().dispatchEvent(EventType.DESELECT, geo);
			geo.setSelected(false);
		}

		selectedGeos.clear();
		updateSelection();
		kernel.notifyRepaint();

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
	 * @param geoND
	 *            geo to be added to selection
	 * @param repaint
	 *            whether repaint is needed
	 * @param updateSelection
	 *            whether selection update is needed
	 */
	public final void addSelectedGeo(GeoElementND geoND, boolean repaint,
			boolean updateSelection) {

		if ((geoND == null) || selectedGeos.contains(geoND)) {
			return;
		}
		GeoElement geo = geoND.toGeoElement();
		dispatchSelected(geo);
		selectedGeos.add(geo);
		geo.setSelected(true);

		if (repaint) {
			kernel.notifyRepaint();
		}

		if (updateSelection) {
			updateSelection();
		}

		// notify all registered selection listeners
		for (GeoElementSelectionListener sl : getSelectionListeners()) {
			if (sl != null) {
				sl.geoElementSelected(geo, true);
			}
		}

	}

	private void dispatchSelected(GeoElement geo) {
		kernel.getApplication().getEventDispatcher()
				.dispatchEvent(EventType.SELECT, geo);

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
		if (getSelectedGeos().size() == 0) {
			return -1; // return -1 if nothing selected
		}

		int layer = getSelectedGeos().get(0).getLayer();

		for (int i = 1; i < getSelectedGeos().size(); i++) {
			GeoElement geo = getSelectedGeos().get(i);
			if (geo.getLayer() != layer) {
				return -2; // return -2 if more than one layer selected
			}
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

		Iterator<GeoElement> it = kernel.getConstruction().getGeoSetLabelOrder()
				.iterator();
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

		Iterator<GeoElement> it = kernel.getConstruction().getGeoSetLabelOrder()
				.iterator();
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
			if (geo.isGeoPolyhedron()
					|| geo.getGeoClassType() == GeoClass.NET) {
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
	final public boolean containsSelectedGeo(GeoElementND geo) {
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
	 * @return if selection contains a locked element
	 */
	final public boolean containsLockedGeo() {
		for (GeoElement geo : selectedGeos) {
			if (geo.isLocked()) {
				return true;
			}
		}
		return false;
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
		if (geo == null) {
			return null;
		}

		clearSelectedGeos(false);
		selectedGeos.add(geo);
		geo.setSelected(true);
		kernel.notifyRepaint();
		for (UpdateSelection listener : listeners) {
			listener.updateSelection(false);
		}

		return geo;
	}

	/**
	 * Adds given geo to selection
	 *
	 * @param geo
	 *            geo
	 */
	public final void addSelectedGeo(GeoElementND geo) {
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
			dispatchSelected(geos.get(i));
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
			kernel.getApplication().getEventDispatcher()
					.dispatchEvent(EventType.DESELECT, geo);
			geo.setSelected(false);
		} else {
			selectedGeos.add(geo);
			dispatchSelected(geo);
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
	 * Selects next geo in a particular order.
	 *
	 * @param ev
	 *            The Euclidian View that has the geos to select.
	 *
	 * @return if select was successful or not.
	 */
	final public boolean selectNextGeo(EuclidianViewInterfaceCommon ev) {
		TreeSet<GeoElement> tree = getEVFilteredTabbingSet();

		if (tree.isEmpty()) {
			getAccessibilityManager().onEmptyConstuction(true);
			return true;
		}

		int selectionSize = selectedGeos.size();

		if (selectionSize == 0) {
			addSelectedGeoForEV(tree.first());
			return false;
		}

		GeoElement lastSelected = selectedGeos.get(selectionSize - 1);
		GeoElement next = tree.higher(lastSelected);

		removeAllSelectedGeos();

		if (next != null) {
			addSelectedGeoForEV(next);
		} else if (!getAccessibilityManager().onSelectLastGeo(true)) {
			addSelectedGeoForEV(tree.first());
		}

		return true;
	}

	/**
	 * Selects last geo in a particular order.
	 *
	 * @param ev
	 *            The Euclidian View that has the geos to select.
	 */
	final public void selectLastGeo(EuclidianViewInterfaceCommon ev) {
		boolean forceLast = false;
		if (selectedGeos.size() != 1) {
			if (!getAccessibilityManager().handleTabExitGeos(false)) {
				return;

			}
			forceLast = true;
		}
		TreeSet<GeoElement> tree = getEVFilteredTabbingSet();

		int selectionSize = selectedGeos.size();
		GeoElement last = tree.last();
		if (forceLast) {
			addSelectedGeoForEV(last);
			return;
		}
		GeoElement lastSelected = selectedGeos.get(selectionSize - 1);
		GeoElement prev = tree.lower(lastSelected);
		removeAllSelectedGeos();

		if (prev != null) {
			addSelectedGeoForEV(prev);
		} else if (!getAccessibilityManager().onSelectFirstGeo(false)) {
			addSelectedGeoForEV(last);
		}
	}

	/**
	 * @param geo
	 *            geo
	 * @return whether next element exists
	 */
	public boolean hasNext(GeoElement geo) {
		TreeSet<GeoElement> tree = getEVFilteredTabbingSet();
		return tree.size() != 0 && tree.last() != geo;
	}

	/**
	 * Select an element and focus it in graphics view
	 * 
	 * @param geo
	 *            construction element
	 */
	public void addSelectedGeoForEV(GeoElement geo) {
		addSelectedGeo(geo);

		checkInputBoxAndFocus(geo);
		App app1 = geo.getKernel().getApplication();

		if (app1.isEuclidianView3Dinited()) {
			EuclidianView3DInterface view3d = app1.getEuclidianView3D();
			if (view3d.isShowing()) {
				view3d.showFocusOn(geo);
			}
		}

	}

	private EuclidianViewInterfaceCommon getViewOf(GeoElement geo) {
		int viewID = geo.getViewSet() != null && geo.getViewSet().size() > 0
				? geo.getViewSet().get(0)
				: -1;
		App app1 = geo.getKernel().getApplication();
		if (viewID == App.VIEW_EUCLIDIAN2) {
			return app1.getEuclidianView2(1);
		} else if (viewID == App.VIEW_EUCLIDIAN3D) {
			return app1.getEuclidianView3D();
		}

		return app1.getEuclidianView1();
	}

	private void checkInputBoxAndFocus(GeoElement geo) {
		EuclidianViewInterfaceCommon view = getViewOf(geo);
		if (geo instanceof GeoInputBox) {
			((EuclidianView) view).focusAndShowTextField((GeoInputBox) geo);
		} else {
			view.requestFocus();
		}
	}

	/**
	 * move selection from input box
	 */
	public void nextFromInputBox() {
		GeoElement next = selectedGeos.isEmpty()
				? null :
				selectedGeos.get(0);
		checkInputBoxAndFocus(next);
	}

	private void filterGeosForView(TreeSet<GeoElement> tree) {

		App app = kernel.getApplication();
		boolean avShowing = algebraViewShowing();

		TreeSet<GeoElement> copy = new TreeSet<>(tree);

		Iterator<GeoElement> it = copy.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			boolean remove = false;
			// selectionAllowed arg only matters for axes; axes are not in
			// construction
			if (!geo.isSelectionAllowed(null)) {
				remove = true;
			} else {
				boolean visibleInView = (app.showView(App.VIEW_EUCLIDIAN3D)
						&& geo.isVisibleInView3D())
						|| (app.showView(App.VIEW_EUCLIDIAN2)
								&& geo.isVisibleInView(App.VIEW_EUCLIDIAN2))
						|| (app.showView(App.VIEW_EUCLIDIAN)
								&& geo.isVisibleInView(App.VIEW_EUCLIDIAN));
				// remove = !avShowing && (!geo.isEuclidianVisible() || !visibleInView);
				remove = !avShowing
						&& (!geo.isEuclidianVisible() || !visibleInView);
			}

			if (remove) {
				tree.remove(geo);
			}
		}

	}

	private boolean algebraViewShowing() {
		return kernel.getApplication().getGuiManager() != null && this.kernel
				.getApplication().getGuiManager().hasAlgebraViewShowing();
	}

	/**
	 * Gets the set of all objects in the order they would appear in AV if AV is
	 * visible. For objects actually accessible by the user use
	 * {@link #getEVFilteredTabbingSet()}
	 * 
	 * TODO add support for layer / object type sorting of AV
	 *
	 * @return set over which TAB iterates: either alphabetical or construction
	 *         order
	 */
	private TreeSet<GeoElement> getTabbingSet() {
		if (algebraViewShowing()) {
			if (this.kernel.getApplication().getSettings().getAlgebra()
					.getTreeMode() == SortMode.ORDER) {
				return kernel.getConstruction().getGeoSetConstructionOrder();
			}
		}
		return kernel.getConstruction().getGeoSetLabelOrder();
	}

	/**
	 * 
	 * @return set over which TAB iterates and belongs to the active Euclidian View.
	 */
	public TreeSet<GeoElement> getEVFilteredTabbingSet() {
		TreeSet<GeoElement> tree = new TreeSet<>(getTabbingSet());
		filterGeosForView(tree);
		return tree;
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
		for (UpdateSelection listener : listeners) {
			listener.updateSelection(updatePropertiesView);
		}
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

	/**
	 * @return selected points
	 */
	public ArrayList<GeoPointND> getSelectedPointList() {
		return selectedPoints;
	}

	/**
	 * @return selected numerics
	 */
	public ArrayList<GeoNumeric> getSelectedNumberList() {
		return selectedNumbers;
	}

	/**
	 * @return selected numbers
	 */
	public ArrayList<GeoNumberValue> getSelectedNumberValueList() {
		return selectedNumberValues;
	}

	/**
	 * @return selected lines
	 */
	public ArrayList<GeoLineND> getSelectedLineList() {
		return selectedLines;
	}

	/**
	 * @return selected paths
	 */
	public ArrayList<Path> getSelectedPathList() {
		return selectedPaths;
	}

	/**
	 * @return selected conics
	 */
	public ArrayList<GeoConicND> getSelectedConicNDList() {
		return selectedConicsND;
	}

	/**
	 * @return selected direction geos
	 */
	public ArrayList<GeoDirectionND> getSelectedDirectionList() {
		return selectedDirections;
	}

	/**
	 * @return selected segments
	 */
	public ArrayList<GeoSegmentND> getSelectedSegmentList() {
		return selectedSegments;
	}

	/**
	 * @return selected regions
	 */
	public ArrayList<Region> getSelectedRegionList() {
		return selectedRegions;
	}

	/**
	 * @return selected implicit curves
	 */
	public ArrayList<GeoImplicit> getSelectedImplicitpolyList() {
		return selectedImplicitpoly;
	}

	/**
	 * @return selected implicit surfaces
	 */
	public ArrayList<GeoImplicitSurfaceND> getSelectedImplicitSurfaceList() {
		return selectedImplicitSurface;
	}

	/**
	 * @return selected functions
	 */
	public ArrayList<GeoFunction> getSelectedFunctionList() {
		return selectedFunctions;
	}

	/**
	 * @return selected nvar functions
	 */
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

	/**
	 * @return temporary moveable geo list
	 */
	public ArrayList<GeoElement> getTempMoveGeoList() {
		if (tempMoveGeoList == null) {
			tempMoveGeoList = new ArrayList<>();
		}
		return tempMoveGeoList;
	}

	/**
	 * @param selectionList
	 *            selection list
	 * @param geo
	 *            (un)selected geo
	 * @param max
	 *            max size of selctionList after addition
	 * @return 0/1/-1 if nothing happened / geo selected / geo unselected
	 */
	public <T> int addToSelectionList(ArrayList<T> selectionList, T geo,
			int max) {
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

	public ArrayList<GeoCoordSys> getSelectedCS2DList() {
		return selectedCS2D;
	}

	public ArrayList<GeoPlaneND> getSelectedPlaneList() {
		return selectedPlane;
	}

	public ArrayList<GeoQuadricND> getSelectedQuadricList() {
		return selectedQuadric;
	}

	public ArrayList<GeoQuadric3DLimitedInterface> getSelectedQuadricLimitedList() {
		return selectedQuadricLim;
	}

	public ArrayList<GeoPolyhedronInterface> getSelectedPolyhedronList() {
		return selectedPolyhedron;
	}

	/**
	 * @param selectionList
	 *            selection list
	 * @param doUpdateSelection
	 *            whether to notify listeners
	 */
	public final void clearSelection(ArrayList<?> selectionList,
			boolean doUpdateSelection) {
		// unselect
		selectionList.clear();
		getSelectedGeoList().clear();
		if (doUpdateSelection) {
			clearSelectedGeos();
		}
	}

	/**
	 * Clears all selection lists.
	 */
	public void clearLists() {
		clearSelection(getSelectedNumberList(), false);
		clearSelection(getSelectedNumberValueList(), false);
		clearSelection(getSelectedPointList(), false);
		clearSelection(getSelectedLineList(), false);
		clearSelection(getSelectedSegmentList(), false);
		clearSelection(getSelectedConicNDList(), false);
		clearSelection(getSelectedVectorList(), false);
		clearSelection(getSelectedPolygonList(), false);
		clearSelection(getSelectedGeoList(), false);
		clearSelection(getSelectedFunctionList(), false);
		clearSelection(getSelectedCurveList(), false);
		clearSelection(getSelectedListList(), false);
		clearSelection(getSelectedPathList(), false);
		clearSelection(getSelectedRegionList(), false);
		clearSelection(getSelectedCS2DList(), false);
		clearSelection(getSelectedPlaneList(), false);
		clearSelection(getSelectedPolyhedronList(), false);
		clearSelection(getSelectedQuadricList(), false);
		clearSelection(getSelectedQuadricLimitedList(), false);
	}

	/**
	 * store selected geos names
	 */
	public void storeSelectedGeosNames() {
		selectedGeosNames.clear();
		for (GeoElement geo : getSelectedGeos()) {
			selectedGeosNames.add(geo.getLabelSimple());
		}
	}

	/**
	 * set geos selected from their names
	 *
	 * @param kernel1
	 *            kernel
	 */
	public void recallSelectedGeosNames(Kernel kernel1) {
		ArrayList<GeoElement> list = new ArrayList<>();
		for (String name : selectedGeosNames) {
			GeoElement geo = kernel1.lookupLabel(name);
			if (geo != null) {
				list.add(geo);
			}
		}
		setSelectedGeos(list);
	}

	/**
	 *
	 * @return if the very first geo is selected.
	 */
	public boolean isFirstGeoSelected() {
		if (selectedGeos.size() == 0) {
			return false;
		}

		TreeSet<GeoElement> tree = getEVFilteredTabbingSet();
		return tree.first().equals(selectedGeos.get(0));
	}

	/**
	 *
	 * @return if the very last geo is selected.
	 */
	public boolean isLastGeoSelected() {
		if (selectedGeos.size() == 0) {
			return false;
		}
		TreeSet<GeoElement> tree = getEVFilteredTabbingSet();
		return tree.last().equals(selectedGeos.get(0));
	}

	/**
	 *
	 *
	 * /** Select last geo of the construction.
	 */
	public void addLastGeoSelected() {
		GeoElement geo = kernel.getConstruction().getGeoSetLabelOrder().last();
		if (geo != null) {
			addSelectedGeo(geo);
		}
	}

	/**
	 * @param listener
	 *            global listener
	 */
	public void addListener(UpdateSelection listener) {
		this.listeners.add(listener);
	}

	/**
	 * Update highlighting of selected geos.
	 */
	public void updateSelectionHighlight() {
		for (GeoElement geo : selectedGeos) {
			kernel.notifyUpdateHightlight(geo);
		}
	}

	private AccessibilityManagerInterface getAccessibilityManager() {
		return kernel.getApplication().getAccessibilityManager();
	}
}

