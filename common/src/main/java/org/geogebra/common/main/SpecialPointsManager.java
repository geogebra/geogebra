package org.geogebra.common.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventListener;
import org.geogebra.common.plugin.EventType;

/**
 * Special point manager.
 *
 */
public class SpecialPointsManager implements UpdateSelection, EventListener {
	private Kernel kernel;
	private GeoElement[] specPoints;
	/** Special points for preview points */
	private List<AlgoElement> specPointAlgos = new ArrayList<>();

	/**
	 * @param kernel
	 *            kernel
	 */
	public SpecialPointsManager(Kernel kernel) {
		this.kernel = kernel;
		kernel.getApplication().getSelectionManager().addListener(this);
		kernel.getApplication().getEventDispatcher().addEventListener(this);
	}

	private GeoElementND[] getSpecPoints(GeoElement geo0,
			List<GeoElement> selectedGeos) {
		specPointAlgos.clear();
		specPoints = null;
		GeoElement geo = (geo0 == null && selectedGeos != null
				&& selectedGeos.size() > 0) ? selectedGeos.get(0) : geo0;

		if (geo != null) {
			ArrayList<GeoElementND> specPoints0 = new ArrayList<>();
			GeoElementND[] sp = getSpecPoints(geo);
			if (sp != null) {
				specPoints0.addAll(Arrays.asList(sp));
			}
			if (specPoints0.size() > 0) {
				specPoints = new GeoElement[specPoints0.size()];
				for (int i = 0; i < specPoints0.size(); i++) {
					specPoints[i] = specPoints0.get(i).toGeoElement();
					specPoints[i].remove();
					specPoints[i].setAdvancedVisualStyle(kernel
							.getConstruction().getConstructionDefaults()
							.getDefaultGeo(
									ConstructionDefaults.DEFAULT_POINT_PREVIEW));
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
		getSpecPoints(geo, kernel.getApplication().getSelectionManager()
				.getSelectedGeos());
		kernel.notifyUpdateSpecPointsPreviewOnEV(specPoints);
	}

	private GeoElementND[] getSpecPoints(GeoElementND geo) {
		if (!(geo instanceof GeoFunction) || !geo.isVisible()
				|| !geo.isDefined()) {
			return null;
		}
		PolyFunction poly = ((GeoFunction) geo).getFunction()
				.expandToPolyFunction(
						((GeoFunction) geo).getFunctionExpression(), false,
						true);
		GeoElementND[] geos1 = null;
		if (kernel.getApplication().getActiveEuclidianView().getShowAxis(0)
				&& (poly == null || poly.getDegree() > 0)) {
			if (!((GeoFunction) geo).isPolynomialFunction(true)
					&& geo.isDefined()) {
				EuclidianViewInterfaceCommon view = kernel.getApplication()
						.getActiveEuclidianView();

				AlgoRoots algoRoots = new AlgoRoots(kernel.getConstruction(),
						null, (GeoFunction) geo, view.getXminObject(),
						view.getXmaxObject(), false);
				geos1 = algoRoots.getRootPoints();
				specPointAlgos.add(algoRoots);
			} else {
				AlgoRootsPolynomial algoRootsPolynomial = new AlgoRootsPolynomial(
						kernel.getConstruction(), null, (GeoFunction) geo,
						false);
				kernel.getConstruction()
						.removeFromAlgorithmList(algoRootsPolynomial);
				geos1 = algoRootsPolynomial.getRootPoints();
				specPointAlgos.add(algoRootsPolynomial);
			}
		}
		GeoElementND[] geos2 = null;
		if (poly == null || poly.getDegree() > 1) {
			if (!((GeoFunction) geo).isPolynomialFunction(true)) {
				EuclidianViewInterfaceCommon view = this.kernel.getApplication()
						.getActiveEuclidianView();
				AlgoExtremumMulti algoExtremumMulti = new AlgoExtremumMulti(
						kernel.getConstruction(), null, (GeoFunction) geo,
						view.getXminObject(), view.getXmaxObject(), false);
				geos2 = algoExtremumMulti.getExtremumPoints();
				specPointAlgos.add(algoExtremumMulti);
			} else {
				AlgoExtremumPolynomial algoExtremumPolynomial = new AlgoExtremumPolynomial(
						kernel.getConstruction(), null, (GeoFunction) geo,
						false);
				kernel.getConstruction()
						.removeFromAlgorithmList(algoExtremumPolynomial);
				geos2 = algoExtremumPolynomial.getRootPoints();
				specPointAlgos.add(algoExtremumPolynomial);
			}
		} else if (kernel.getApplication().getActiveEuclidianView()
				.getShowAxis(1)) {
			AlgoIntersectPolynomialLine algoPolynomialLine = new AlgoIntersectPolynomialLine(
					kernel.getConstruction(), (GeoFunction) geo,
					kernel.getConstruction().getYAxis());
			kernel.getConstruction()
					.removeFromAlgorithmList(algoPolynomialLine);
			geos2 = algoPolynomialLine.getOutput();
			specPointAlgos.add(algoPolynomialLine);
		}
		if (geos1 != null && geos1.length > 0) {
			if (geos2 != null && geos2.length > 0) {
				GeoElementND[] ret = new GeoElementND[geos1.length
						+ geos2.length];
				for (int i = 0; i < geos1.length; i++) {
					ret[i] = geos1[i];
				}
				for (int i = 0; i < geos2.length; i++) {
					ret[i + geos1.length] = geos2[i];
				}
				return ret;
			}
			return geos1;
		}
		return geos2;
	}

	/**
	 * @return the List of Preview points of the currently selected functions or
	 *         null if there is none
	 */
	public List<GeoElement> getSelectedPreviewPoints() {
		return specPoints == null ? null : Arrays.asList(specPoints);
	}

	public void updateSelection(boolean updateProperties) {
		updateSelection();
	}

	/**
	 * Updates special points when the selection updated.
	 */
	public void updateSelection() {
		updateSpecialPoints(null);
	}

	public void sendEvent(Event evt) {
		if (evt.type == EventType.DESELECT) {
			updateSelection();
		}
	}

	public void reset() {
		// not needed
	}
}
