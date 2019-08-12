package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoDispatcher;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.CmdIntersect;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;

/**
 * Special point manager.
 *
 */
public class SpecialPointsManager implements UpdateSelection, EventListener, CoordSystemListener {

	private Kernel kernel;
	private List<GeoElement> specPoints;
	private List<SpecialPointsListener> specialPointsListeners = new ArrayList<>();
	private boolean isUpdating = false;
    /**
     * storing the special points parent algos: needed for iOS as GeoElement as only weak
     * reference to its parent algo
     */
	private List<AlgoElement> specPointAlgos;

	/**
	 * @param kernel
	 *            kernel
	 */
	public SpecialPointsManager(Kernel kernel) {
		this.kernel = kernel;
		specPointAlgos = new ArrayList<>();
		App app = kernel.getApplication();
		app.getSelectionManager().addListener(this);
		app.getEventDispatcher().addEventListener(this);
		app.getActiveEuclidianView().getEuclidianController().addZoomerListener(this);
	}

	private List<GeoElement> getSpecPoints(GeoElement geo0,
			List<GeoElement> selectedGeos) {

        // we set parent algorithm to null due to weak reference in iOS
        if (specPoints != null) {
            for (GeoElement geo : specPoints) {
                geo.setParentAlgorithm(null);
            }
        }
        specPointAlgos.clear();
		specPoints = null;
		GeoElement geo = (geo0 == null && selectedGeos != null
				&& selectedGeos.size() > 0) ? selectedGeos.get(0) : geo0;

		if (geo != null) {
			ArrayList<GeoElementND> specPoints0 = new ArrayList<>();
			getSpecPoints(geo, specPoints0);

			if (specPoints0.size() > 0) {
				specPoints = new ArrayList<>(specPoints0.size());
				for (GeoElementND pt : specPoints0) {
					if (pt != null) {
						specPoints.add(pt.toGeoElement());
						pt.remove();
						pt.setAdvancedVisualStyle(kernel
							.getConstruction().getConstructionDefaults()
							.getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_PREVIEW));
					}
				}
			}
		}

		return specPoints;
	}

	/**
	 * Updates the special points of the geo.
	 *
	 * @param geo
	 *            geo which special points will be updated
	 */
	public void updateSpecialPoints(GeoElement geo) {
		if (!kernel.getApplication().getConfig().hasPreviewPoints()
				|| isUpdating) {
			return;
		}
		// Prevent calling update special points recursively
		isUpdating = true;

		getSpecPoints(geo, kernel.getApplication().getSelectionManager()
				.getSelectedGeos());

		fireSpecialPointsChangedEvent();

		isUpdating = false;
	}

	private void getSpecPoints(GeoElementND geo,
							   ArrayList<GeoElementND> retList) {
		if (!shouldShowSpecialPoints(geo)) {
			return;
		}
		boolean xAxis = kernel.getApplication().getActiveEuclidianView()
				.getShowAxis(0);
		boolean yAxis = kernel.getApplication().getActiveEuclidianView()
				.getShowAxis(1);
		if (!xAxis && !yAxis) {
			return;
		}
		Construction cons = kernel.getConstruction();
		boolean silentMode = kernel.isSilentMode();
		boolean suppressLabelsActive = cons.isSuppressLabelsActive();
		kernel.setSilentMode(true);
		try {
			doGetSpecialPoints(geo.unwrapSymbolic(), xAxis, yAxis, retList);
			// Can be of function or equation
			if (hasIntersectsBetween(geo)) {
				getIntersectsBetween(geo, retList);
			}
		} catch (Throwable exception) {
			// ignore
		} finally {
			kernel.setSilentMode(silentMode);
			cons.setSuppressLabelCreation(suppressLabelsActive);
		}
	}

	private void doGetSpecialPoints(GeoElementND geo, boolean xAxis,
									boolean yAxis, ArrayList<GeoElementND> retList) {
		if (geo instanceof GeoFunction) {
			getFunctionSpecialPoints((GeoFunction) geo, xAxis, yAxis, retList);
		} else if (geo instanceof EquationValue) {
			getEquationSpecialPoints(geo, xAxis, yAxis, retList);
		}
	}

	private void getFunctionSpecialPoints(GeoFunction geo, boolean xAxis, boolean yAxis,
								  ArrayList<GeoElementND> retList) {
		PolyFunction poly = geo.getFunction()
				.expandToPolyFunction(
						geo.getFunctionExpression(), false,
						true);
		if (xAxis && (poly == null || poly.getDegree() > 0)) {
			if (!geo.isPolynomialFunction(true)
					&& geo.isDefined()) {
				EuclidianViewInterfaceCommon view = kernel.getApplication()
						.getActiveEuclidianView();

				AlgoRoots algoRoots = new AlgoRoots(kernel.getConstruction(),
						null, geo, view.getXminObject(),
						view.getXmaxObject(), false);
				processAlgo(geo, algoRoots, retList);
			} else {
				AlgoRootsPolynomial algoRootsPolynomial = new AlgoRootsPolynomial(
						kernel.getConstruction(), null, geo,
						false);
				processAlgo(geo, algoRootsPolynomial, retList);
			}
		}
		if (poly == null || poly.getDegree() > 1) {
			if (!geo.isPolynomialFunction(true)) {
				EuclidianViewInterfaceCommon view = this.kernel.getApplication()
						.getActiveEuclidianView();
				AlgoExtremumMulti algoExtremumMulti = new AlgoExtremumMulti(
						kernel.getConstruction(), null, geo,
						view.getXminObject(), view.getXmaxObject(), false);
				processAlgo(geo, algoExtremumMulti, retList);
			} else {
				addExtremumPoly(geo, retList);
			}
		}

		if (yAxis) {
			AlgoIntersectPolynomialLine algoPolynomialLine = new AlgoIntersectPolynomialLine(
					kernel.getConstruction(), geo,
					kernel.getConstruction().getYAxis());
			processAlgo(geo, algoPolynomialLine, retList);
		}
	}

	private void addExtremumPoly(GeoFunctionable geo,
			ArrayList<GeoElementND> retList) {
		AlgoExtremumPolynomial algoExtremumPolynomial = new AlgoExtremumPolynomial(
				kernel.getConstruction(), null, geo, false);
		processAlgo(geo, algoExtremumPolynomial, retList);
	}

	private void getEquationSpecialPoints(GeoElementND geo, boolean xAxis,
			boolean yAxis, ArrayList<GeoElementND> retList) {
		GeoLine xAxisLine = kernel.getXAxis();
		GeoLine yAxisLine = kernel.getYAxis();
		if (geo == xAxisLine || geo == yAxisLine) {
			return;
		}
		Command cmd = new Command(kernel, "Intersect", false);
		CmdIntersect intersect = new CmdIntersect(kernel);

		if (xAxis) {
			getSpecialPointsIntersect(geo, xAxisLine, intersect, cmd, retList);
		}
		if (yAxis) {
			getSpecialPointsIntersect(geo, yAxisLine, intersect, cmd, retList);
		}
		if (geo.isGeoConic() && geo.isRealValuedFunction()) {
			addExtremumPoly((GeoConic) geo, retList);
		}
	}

	private void getIntersectsBetween(GeoElementND geo,
			ArrayList<GeoElementND> retList) {
		Construction cons = kernel.getConstruction();
		GeoLine xAxisLine = kernel.getXAxis();
		GeoLine yAxisLine = kernel.getYAxis();
		if (geo == xAxisLine || geo == yAxisLine) {
			return;
		}
		Command cmd = new Command(kernel, "Intersect", false);
		CmdIntersect intersect = new CmdIntersect(kernel);

		Set<GeoElement> elements = new TreeSet<>(cons.getGeoSetConstructionOrder());
		for (GeoElement element: elements) {
			if (hasIntersectsBetween(element) && element != geo && element.isEuclidianVisible()) {
				getSpecialPointsIntersect(geo, element, intersect, cmd, retList);
			}
		}
	}

	private void getSpecialPointsIntersect(GeoElementND element,
			GeoElement secondElement,
										   CmdIntersect intersect, Command cmd,
										   ArrayList<GeoElementND> retList) {
		AlgoDispatcher dispatcher = kernel.getAlgoDispatcher();
		boolean oldValue = dispatcher.isIntersectCacheEnabled();
		try {
			dispatcher.setIntersectCacheEnabled(false);
			GeoElement[] elements = intersect
					.intersect2(new GeoElement[] { element.toGeoElement(),
							secondElement }, cmd);
			for (GeoElement output : elements) {
				AlgoElement parent = output.getParentAlgorithm();
				element.removeAlgorithm(parent);
				secondElement.removeAlgorithm(parent);
				storeAlgo(parent);
			}
			add(elements, retList);
		} catch (Throwable exception) {
			// ignore
		} finally {
			dispatcher.setIntersectCacheEnabled(oldValue);
		}
	}

	private static boolean shouldShowSpecialPoints(GeoElementND geo) {
		GeoElementND geoTwin = geo.unwrapSymbolic();
		return (geoTwin instanceof GeoFunction || geoTwin instanceof EquationValue
				|| geoTwin instanceof GeoSymbolic)
				&& !(geoTwin.isGeoSegment())
				&& geoTwin.isVisible() && geoTwin.isDefined()
				&& geoTwin.isEuclidianVisible() && !geoTwin.isGeoElement3D();
	}

	private static boolean hasIntersectsBetween(GeoElementND element) {
		return element instanceof EquationValue || element instanceof Functional;
	}

	private void processAlgo(GeoElementND element, AlgoElement algoElement,
			ArrayList<GeoElementND>
			retList) {
		element.removeAlgorithm(algoElement);
		add(algoElement.getOutput(), retList);
		storeAlgo(algoElement);
	}

	private static void add(GeoElement[] geos1,
			ArrayList<GeoElementND> retList) {
		if (geos1 != null) {
			for (int i = 0; i < geos1.length; i++) {
				retList.add(geos1[i]);
			}
		}
	}

	/**
	 * @return the List of Preview points of the currently selected functions or
	 *         null if there is none
	 */
	public List<GeoElement> getSelectedPreviewPoints() {
		return specPoints;
	}

	@Override
	public void updateSelection(boolean updateProperties) {
		updateSelection();
	}

	/**
	 * Updates special points when the selection updated.
	 */
	public void updateSelection() {
		updateSpecialPoints(null);
	}

	@Override
	public void sendEvent(Event evt) {
		if (evt.type == EventType.DESELECT) {
			updateSelection();
		}
	}

	@Override
	public void reset() {
		// not needed
	}

	@Override
	public void onCoordSystemChanged() {
		updateSpecialPoints(null);
	}

	public void registerSpecialPointsListener(SpecialPointsListener listener) {
		specialPointsListeners.add(listener);
	}

	public void deregisterSpecialPointsListener(SpecialPointsListener listener) {
		specialPointsListeners.remove(listener);
	}

	private void fireSpecialPointsChangedEvent() {
		for (SpecialPointsListener listener: specialPointsListeners) {
			listener.specialPointsChanged(this, specPoints);
		}
	}

	private void storeAlgo(AlgoElement algo) {
	    // we need to store parent algos due to weak reference in iOS
		specPointAlgos.add(algo);
	}

}
