/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.draw.DrawButton;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElement.HitType;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.TestGeo;

import com.google.j2objc.annotations.Weak;

public class HitDetector {
	private ArrayList<GeoElement> hitPointOrBoundary;
	private ArrayList<GeoElement> hitFilling;
	private ArrayList<GeoElement> hitLabel;
	@Weak
	private final EuclidianView ev;
	private Hits hits;

	public HitDetector(EuclidianView ev) {
		this.ev = ev;
	}

	private void setHits(GPoint p, int hitThreshold) {
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

		for (Drawable d : ev.getAllDrawableList()) {
			if (d.isEuclidianVisible()) {
				if (d.hit(p.x, p.y, hitThreshold)) {
					GeoElement geo = d.getGeoElement();
					hitMask = hitMask || (geo.isMask() || geo.isMeasurementTool())
							&& !ev.hasSpotlight();

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
			if (geo.isSelectionAllowed(ev)) {
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
			if (geo.getLayer() < maxlayer || (hitMask && !geo.isMask() && !geo
					.isMeasurementTool())) {
				hits.remove(i);
			}
		}

		// remove all lists if there are other objects too
		if (hits.size() > hits.getListCount()) {
			for (int i = hits.size() - 1; i >= 0; i--) {
				GeoElement geo = hits.get(i);
				if (geo.isGeoList() && !((GeoList) geo).drawAsComboBox()) {
					hits.remove(i);
				}
			}
		}
	}

	private void addXAxis(GPoint p, int hitThreshold) {
		if (ev.showAxes[0] && (Math.abs(ev.getYAxisCrossingPixel() - p.y) < hitThreshold)) {
			// handle positive axis only
			if (!ev.positiveAxes[0] || (ev.getXAxisCrossingPixel() < p.x - hitThreshold)) {
				hits.add(ev.getKernel().getXAxis());
			}
		}
	}

	private void addYAxis(GPoint p, int hitThreshold) {
		if (ev.showAxes[1] && (Math.abs(ev.getXAxisCrossingPixel() - p.x) < hitThreshold)) {
			// handle positive axis only
			if (!ev.positiveAxes[1] || (ev.getYAxisCrossingPixel() > p.y - hitThreshold)) {
				hits.add(ev.getKernel().getYAxis());
			}
		}
	}

	/**
	 * @return button from hits, closest to the user
	 */
	public DrawButtonWidget getHitButton() {
		int size = hits.size();
		for (int i = size - 1; i >= 0; i--) {
			GeoElement geoElement = hits.get(i);
			if (geoElement instanceof GeoButton) {
				DrawableND drawable = ev.getDrawableFor(geoElement);
				if (drawable instanceof DrawButton) {
					return ((DrawButton) drawable).getWidget();
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
		addIntersectionHits(rect, TestGeo.OBJECT);
	}

	/**
	 * sets array of GeoElements whose visual representation is inside of the
	 * given screen rectangle
	 * 
	 * @param rect
	 *            rectangle
	 * @param filter
	 *            filter to only check some geos
	 */
	public void addIntersectionHits(GRectangle rect, TestGeo filter) {
		if (rect == null) {
			return;
		}

		for (Drawable d : ev.getAllDrawableList()) {
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && geo.isSelectionAllowed(ev) && filter.test(geo)
					&& !hits.contains(geo)
					&& d.intersectsRectangle(rect)) {
				d.setPartialHitClip(rect);
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

		for (Drawable d : ev.getAllDrawableList()) {
			GeoElement geo = d.getGeoElement();
			if (geo.isEuclidianVisible() && d.isInside(rect)) {
				hits.add(geo);
			}
		}
	}

	/**
	 * Reset the hits.
	 */
	public void reset() {
		hits = new Hits();
	}

	private void setOnlyHit(Drawable resizedShape) {
		hits.init();
		if (resizedShape != null) {
			hits.add(resizedShape.getGeoElement());
		}
	}

	/**
	 * Update hits based on cursor position and event type
	 * 
	 * @param p
	 *            position
	 * @param type
	 *            event type
	 */
	public void setHits(GPoint p, PointerEventType type) {
		if (ev.getBoundingBoxHandlerHit(p, type) != null) {
			setOnlyHit(ev.getEuclidianController().getResizedShape());
		} else {
			int capturingThreshold = ev.getApplication().getCapturingThreshold(type);
			setHits(p, capturingThreshold);
			if (type == PointerEventType.TOUCH && getHits().size() == 0) {
				setHits(p, capturingThreshold * 3);
			}
		}
	}
}
