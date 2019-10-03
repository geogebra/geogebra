package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.DrawableList.DrawableIterator;
import org.geogebra.common.euclidian.draw.DrawButton;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoList;

public class HitDetector {
	private ArrayList<GeoElement> hitPointOrBoundary;
	private ArrayList<GeoElement> hitFilling;
	private ArrayList<GeoElement> hitLabel;
	private final EuclidianView view;
	private Hits hits;

	public HitDetector(EuclidianView view) {
		this.view = view;
	}

	/**
	 * sets the hits of GeoElements whose visual representation is at screen
	 * coords (x,y). order: points, vectors, lines, conics
	 *
	 * @param p
	 *            clicked point
	 * @param hitThreshold
	 *            threshold
	 */
	public void setHits(GPoint p, int hitThreshold) {
		hits.init();
		if (hitPointOrBoundary == null) {
			hitPointOrBoundary = new ArrayList<>();
			hitFilling = new ArrayList<>();
			hitLabel = new ArrayList<>();
		} else {
			hitPointOrBoundary.clear();
			hitFilling.clear();
			hitLabel.clear();
		}
		if (p == null) {
			return;
		}
		boolean hitMask = false;
		DrawableIterator it = view.allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			if (d.isEuclidianVisible()) {
				if (d.hit(p.x, p.y, hitThreshold)) {
					GeoElement geo = d.getGeoElement();

					hitMask = hitMask || geo.isMask();

					if (geo.getLastHitType() == HitType.ON_BOUNDARY) {
						hitPointOrBoundary.add(geo);
					} else {
						hitFilling.add(geo);
					}
				} else if (d.hitLabel(p.x, p.y)) {
					GeoElement geo = d.getGeoElement();
					hitLabel.add(geo);
				}
			}
		}
		// labels first
		for (GeoElement geo : hitLabel) {
			hits.add(geo);
		}

		// then points and paths
		for (GeoElement geo : hitPointOrBoundary) {
			hits.add(geo);
		}

		// then regions
		for (GeoElement geo : hitFilling) {
			if (geo.isSelectionAllowed(view)) {
				hits.add(geo);
			}
		}

		// look for axis
		if (hits.getImageCount() == 0) {
			addXAxis(p, hitThreshold);
			addYAxis(p, hitThreshold);
		}

		// keep geoelements only on the top layer
		int maxlayer = 0;
		for (int i = 0; i < hits.size(); ++i) {
			GeoElement geo = hits.get(i);
			if (maxlayer < geo.getLayer()) {
				maxlayer = geo.getLayer();
			}
		}
		for (int i = hits.size() - 1; i >= 0; i--) {
			GeoElement geo = hits.get(i);
			if (geo.getLayer() < maxlayer || (hitMask && !geo.isMask())) {
				hits.remove(i);
			}
		}

		// remove all lists and images if there are other objects too
		if ((hits.size() - (hits.getListCount() + hits.getImageCount())) > 0) {
			for (int i = hits.size() - 1; i >= 0; i--) {
				GeoElement geo = hits.get(i);
				if ((geo.isGeoList() && !((GeoList) geo).drawAsComboBox()) || geo.isGeoImage()) {
					hits.remove(i);
				}
			}
		}
	}

	private void addXAxis(GPoint p, int hitThreshold) {
		if (view.showAxes[0] && (Math.abs(view.getYAxisCrossingPixel() - p.y) < hitThreshold)) {
			// handle positive axis only
			if (!view.positiveAxes[0] || (view.getXAxisCrossingPixel() < p.x - hitThreshold)) {
				hits.add(view.getKernel().getXAxis());
			}
		}
	}

	private void addYAxis(GPoint p, int hitThreshold) {
		if (view.showAxes[1] && (Math.abs(view.getXAxisCrossingPixel() - p.x) < hitThreshold)) {
			// handle positive axis only
			if (!view.positiveAxes[1] || (view.getYAxisCrossingPixel() > p.y - hitThreshold)) {
				hits.add(view.getKernel().getYAxis());
			}
		}
	}

	/**
	 * @return button from hits, closest to the user
	 */
	public MyButton getHitButton() {
		int size = hits.size();
		for (int i = size - 1; i >= 0; i--) {
			GeoElement geoElement = hits.get(i);
			if (geoElement instanceof GeoButton) {
				DrawableND drawable = view.getDrawableFor(geoElement);
				if (drawable instanceof DrawButton) {
					return ((DrawButton) drawable).myButton;
				}
			}
		}

		return null;
	}

	/**
	 * sets array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 * 
	 * @param rect
	 *            rectangle
	 */
	public void setIntersectionHits(GRectangle rect) {
		hits.init();
		if (rect == null) {
			return;
		}

		DrawableIterator it = view.allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.intersectsRectangle(rect)) {
				hits.add(geo);
			}
		}
	}

	/**
	 * @return objects that were hit
	 */
	public Hits getHits() {
		return hits;
	}

	/**
	 * sets array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 * 
	 * @param rect
	 *            selection area
	 */
	public void setHits(GRectangle rect) {
		hits.init();
		if (rect == null) {
			return;
		}

		DrawableIterator it = view.allDrawableList.getIterator();
		while (it.hasNext()) {
			Drawable d = it.next();
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {
				hits.add(geo);
			}
		}
	}

	public void reset() {
		hits = new Hits();
	}
}
