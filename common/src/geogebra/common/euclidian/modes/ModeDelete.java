package geogebra.common.euclidian.modes;

import geogebra.common.awt.GRectangle;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.Hits;
import geogebra.common.euclidian.event.AbstractEvent;
import geogebra.common.euclidian.event.PointerEventType;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoAttachCopyToView;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.AlgoPolyLine;
import geogebra.common.kernel.algos.AlgorithmSet;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPenStroke;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.App;

import java.util.Iterator;

public class ModeDelete {
	private EuclidianView view;
	private Kernel kernel;
	private EuclidianController ec;
	private boolean objDeleteMode = false, penDeleteMode = false;

	public ModeDelete(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.kernel = view.getKernel();
		this.view = view;
	}

	GRectangle rect = AwtFactory.prototype.newRectangle(0, 0, 100, 100);
	public void handleMouseDraggedForDelete(AbstractEvent e, int deleteSize,
			boolean forceOnlyStrokes) {
		if (e == null) {
			return;
		}
		int eventX = e.getX();
		int eventY = e.getY();
		rect.setBounds(eventX - deleteSize / 2, eventY - deleteSize / 2,
				deleteSize, deleteSize);
		view.setDeletionRectangle(rect);
		view.setIntersectionHits(rect);
		Hits h = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(h);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setTransparentCursor();

		Iterator<GeoElement> it = h.iterator();

		AlgorithmSet as = null;
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo instanceof GeoPenStroke) {
				GeoPenStroke gps = (GeoPenStroke) geo;

				// we need two arrays for the case that AlgoAttachCopyToView is
				// involved
				// the original points (dataPoints) are saved, but will be
				// translated
				// and everything by the algorithm so that the
				// GeoPenStroke-output
				// holds the points which are really drawn (and should be used
				// for
				// hit detection).

				GeoPoint[] realPoints = (GeoPoint[]) gps.getPoints();
				GeoPoint[] dataPoints;

				if (geo.getParentAlgorithm() != null
						&& (geo.getParentAlgorithm() instanceof AlgoAttachCopyToView)) {
					AlgoElement ae = geo.getParentAlgorithm();
					for (int i = 0; i < ae.getInput().length; i++) {
						if (ae.getInput()[i] instanceof GeoPenStroke) {
							gps = (GeoPenStroke) ae.getInput()[i];
						}
					}
				}
				if (gps.getParentAlgorithm() != null
						&& gps.getParentAlgorithm() instanceof AlgoPolyLine) {
					dataPoints = ((AlgoPolyLine) gps.getParentAlgorithm())
							.getPoints();
				} else {
					dataPoints = (GeoPoint[]) gps.getPoints();
				}

				// find out if this stroke is still visible
				// after removing points
				boolean hasVisibleLine = false;
				boolean lastWasVisible = false;
				if (realPoints.length == dataPoints.length) {
					for (int i = 0; i < dataPoints.length; i++) {
						GeoPoint p = realPoints[i];
						if (p.isDefined()
								&& Math.max(
										Math.abs(eventX
												- view.toScreenCoordXd(p.inhomX)),
										Math.abs(eventY
												- view.toScreenCoordYd(p.inhomY))) <= deleteSize / 2) {
							dataPoints[i].setUndefined();
							if (as == null) {
								as = dataPoints[i].getAlgoUpdateSet();
							} else {
								as.addAll(dataPoints[i].getAlgoUpdateSet());
							}
						}
						if (lastWasVisible && dataPoints[i].isDefined()) {
							hasVisibleLine = true;
						}
						lastWasVisible = dataPoints[i].isDefined();
					}
				} else {
					App.debug("Can't delete points on stroke. Different number of in and output points.");
				}
				if (hasVisibleLine) { // still something visible, don't delete
					it.remove(); // remove this Stroke from hits
				}
			} else {
				if (!this.penDeleteMode) {
					this.objDeleteMode = true;
				}
				if (onlyStrokes) {
					it.remove();
				}
			}
		}

		ec.deleteAll(h);
		if (as != null)
			as.updateAll();


	}

	private void updatePenDeleteMode(Hits h) {
		// if we switched to pen deletion just now, some geos may still need
		// removing
		Iterator<GeoElement> it2 = h.iterator();
		while (it2.hasNext()) {
			GeoElement geo2 = it2.next();
			if (geo2 instanceof GeoPenStroke) {
				this.penDeleteMode = true;
			}
		}

	}

	public void mousePressed(PointerEventType type) {
		this.objDeleteMode = false;
		this.penDeleteMode = false;

	}

	public final boolean process(Hits hits, boolean control) {
		if (hits.isEmpty() || this.penDeleteMode) {
			return false;
		}

		ec.addSelectedGeo(hits, 1, false);
		if (ec.selGeos() == 1) {
			// delete this object
			GeoElement[] geos = ec.getSelectedGeos();
			geos[0].removeOrSetUndefinedIfHasFixedDescendent();
			return true;
		}
		return false;
	}
}
