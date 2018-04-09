package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.commands.CmdIntersect;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
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
	/** Special points for preview points */
	private List<AlgoElement> specPointAlgos = new ArrayList<>();

	private List<SpecialPointsListener> specialPointsListeners = new ArrayList<>();

	/**
	 * @param kernel
	 *            kernel
	 */
	public SpecialPointsManager(Kernel kernel) {
		this.kernel = kernel;
		App app = kernel.getApplication();
		app.getSelectionManager().addListener(this);
		app.getEventDispatcher().addEventListener(this);
		app.getActiveEuclidianView().getEuclidianController().addZoomerListener(this);
	}

	private List<GeoElement> getSpecPoints(GeoElement geo0,
			List<GeoElement> selectedGeos) {
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
		if ("3D".equals(kernel.getApplication().getVersion().getAppName())) {
			return;
		}
		getSpecPoints(geo, kernel.getApplication().getSelectionManager()
				.getSelectedGeos());

		fireSpecialPointsChangedEvent();
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
		if (geo instanceof GeoFunction) {
			getFunctionSpecialPoints((GeoFunction) geo, xAxis, yAxis, retList);
		} else if (geo instanceof EquationValue) {
            getEquationSpecialPoints((GeoElement) geo, xAxis, yAxis, retList);
		}
	}

	private void getFunctionSpecialPoints(GeoFunction geo, boolean xAxis, boolean yAxis,
								  ArrayList<GeoElementND> retList) {
		PolyFunction poly = (geo).getFunction()
				.expandToPolyFunction(
						(geo).getFunctionExpression(), false,
						true);
		if (xAxis && (poly == null || poly.getDegree() > 0)) {
			if (!(geo).isPolynomialFunction(true)
					&& geo.isDefined()) {
				EuclidianViewInterfaceCommon view = kernel.getApplication()
						.getActiveEuclidianView();

				AlgoRoots algoRoots = new AlgoRoots(kernel.getConstruction(),
						null, geo, view.getXminObject(),
						view.getXmaxObject(), false);
				add(algoRoots.getRootPoints(), retList);
				specPointAlgos.add(algoRoots);
			} else {
				AlgoRootsPolynomial algoRootsPolynomial = new AlgoRootsPolynomial(
						kernel.getConstruction(), null, geo,
						false);
				kernel.getConstruction()
						.removeFromAlgorithmList(algoRootsPolynomial);
				add(algoRootsPolynomial.getRootPoints(), retList);
				specPointAlgos.add(algoRootsPolynomial);
			}
		}
		if (poly == null || poly.getDegree() > 1) {
			if (!(geo).isPolynomialFunction(true)) {
				EuclidianViewInterfaceCommon view = this.kernel.getApplication()
						.getActiveEuclidianView();
				AlgoExtremumMulti algoExtremumMulti = new AlgoExtremumMulti(
						kernel.getConstruction(), null, geo,
						view.getXminObject(), view.getXmaxObject(), false);
				add(algoExtremumMulti.getExtremumPoints(), retList);
				specPointAlgos.add(algoExtremumMulti);
			} else {
				AlgoExtremumPolynomial algoExtremumPolynomial = new AlgoExtremumPolynomial(
						kernel.getConstruction(), null, geo,
						false);
				kernel.getConstruction()
						.removeFromAlgorithmList(algoExtremumPolynomial);
				add(algoExtremumPolynomial.getRootPoints(), retList);
				specPointAlgos.add(algoExtremumPolynomial);
			}
		}

		if (yAxis) {
			AlgoIntersectPolynomialLine algoPolynomialLine = new AlgoIntersectPolynomialLine(
					kernel.getConstruction(), geo,
					kernel.getConstruction().getYAxis());
			kernel.getConstruction()
					.removeFromAlgorithmList(algoPolynomialLine);
			add(algoPolynomialLine.getOutput(), retList);
			specPointAlgos.add(algoPolynomialLine);
		}
	}

	private void getEquationSpecialPoints(GeoElement geo, boolean xAxis,
			boolean yAxis, ArrayList<GeoElementND> retList) {
		Construction cons = kernel.getConstruction();
		GeoLine xAxisLine = kernel.getXAxis();
		GeoLine yAxisLine = kernel.getYAxis();
		if (geo == xAxisLine || geo == yAxisLine) {
			return;
		}
		Command cmd = new Command(kernel, "Intersect", false);
		CmdIntersect intersect = new CmdIntersect(kernel);
		boolean wasSuppressLabelActive = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		if (xAxis) {
			getSpecialPointsIntersect(geo, xAxisLine, intersect, cmd, retList);
		}
		if (yAxis) {
			getSpecialPointsIntersect(geo, yAxisLine, intersect, cmd, retList);
		}

		for (GeoElement element: cons.getGeoSetConstructionOrder()) {
			if (element instanceof EquationValue && element != geo) {
				getSpecialPointsIntersect(geo, element, intersect, cmd, retList);
			}
		}

		cons.setSuppressLabelCreation(wasSuppressLabelActive);
	}

	private void getSpecialPointsIntersect(GeoElement element, GeoElement secondElement,
										   CmdIntersect intersect, Command cmd,
										   ArrayList<GeoElementND> retList) {
		try {
			GeoElement[] elements = intersect
					.intersect2(new GeoElement[]{element, secondElement}, cmd);

			for (GeoElement output : elements) {
				AlgoElement parent = output.getParentAlgorithm();
				specPointAlgos.add(parent);
				kernel.getConstruction().removeFromAlgorithmList(parent);
			}
			add(elements, retList);
		} catch (Throwable exception) {
			// ignore
		}
	}

	private static boolean shouldShowSpecialPoints(GeoElementND geo) {
		return (geo instanceof GeoFunction || geo instanceof EquationValue)
				&& geo.isVisible() && geo.isDefined()
				&& geo.isEuclidianVisible() && !geo.isGeoElement3D();
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
}
