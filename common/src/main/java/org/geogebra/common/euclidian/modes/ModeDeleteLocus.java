package org.geogebra.common.euclidian.modes;

import java.util.Iterator;

import org.geogebra.common.awt.GEllipse2DDouble;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianCursor;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Hits;
import org.geogebra.common.euclidian.event.AbstractEvent;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoLocusStroke;

/**
 * Delete mode controller for locus based penstrokes
 */
public class ModeDeleteLocus {

	private EuclidianView view;
	private EuclidianController ec;
	private boolean objDeleteMode = false;
	private boolean penDeleteMode = false;
	private GEllipse2DDouble ellipse = AwtFactory.getPrototype().newEllipse2DDouble(0, 0, 10, 10);

	/**
	 * @param view
	 *            EV
	 */
	public ModeDeleteLocus(EuclidianView view) {
		this.ec = view.getEuclidianController();
		this.view = view;
	}

	/**
	 * @param e
	 *            mouse event
	 * @param forceOnlyStrokes
	 *            whether to only delete strokes
	 */
	public void handleMouseDraggedForDelete(AbstractEvent e, boolean forceOnlyStrokes) {
		if (e == null) {
			return;
		}

		int eventX = e.getX();
		int eventY = e.getY();
		double size = ec.getDeleteToolSize();
		ellipse.setFrame(eventX - size / 2, eventY - size / 2, size, size);

		view.setDeletionRectangle(ellipse);
		view.getHitDetector().setIntersectionHits(ellipse.getBounds());
		Hits h = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(h);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setCursor(EuclidianCursor.TRANSPARENT);

		Iterator<GeoElement> it = h.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();

			if (geo instanceof GeoLocusStroke) {
				deletePartOfPenStroke((GeoLocusStroke) geo, eventX, eventY, size);
				geo.updateRepaint();
				it.remove();
			} else {
				if (!this.penDeleteMode) {
					this.objDeleteMode = true;
				}
				if (onlyStrokes) {
					it.remove();
				}
			}
		}
		// do not delete images using eraser
		h.removeImages();
		ec.deleteAll(h);
	}

	/**
	 * @param hits
	 *            hit objects
	 * @param selPreview
	 *            for preview
	 * @return whether something was deleted
	 */
	public boolean process(Hits hits, boolean selPreview) {
		if (hits.isEmpty() || this.penDeleteMode) {
			return false;
		}
		ec.addSelectedGeo(hits, 1, false, selPreview);
		if (ec.selGeos() == 1) {
			GeoElement[] geos = ec.getSelectedGeos();
			// delete only parts of GeoLocusStroke, not the whole object
			// when eraser tool is used
			if (geos[0] instanceof GeoLocusStroke
					&& ec.getMode() == EuclidianConstants.MODE_ERASER) {
				updatePenDeleteMode(hits);
				if (ec.getMouseLoc() == null) {
					return false;
				}

				int eventX = ec.getMouseLoc().getX();
				int eventY = ec.getMouseLoc().getY();
				ellipse.setFrame(eventX, eventY, ec.getDeleteToolSize(), ec.getDeleteToolSize());

				deletePartOfPenStroke((GeoLocusStroke) geos[0], eventX, eventY, ec.getDeleteToolSize());
				geos[0].updateRepaint();
			}
			// delete this object
			else {
				if (!(geos[0] instanceof GeoImage)) {
					geos[0].removeOrSetUndefinedIfHasFixedDescendent();
				}
			}
			return true;
		}
		return false;
	}

	private void deletePartOfPenStroke(GeoLocusStroke gls, int x1, int y1, double size) {
		double x = view.toRealWorldCoordX(x1);
		double y = view.toRealWorldCoordY(y1);
		gls.deletePart(x, y, size * view.getInvXscale());
	}

	private void updatePenDeleteMode(Hits h) {
		// if we switched to pen deletion just now, some geos may still need
		// removing
		for (GeoElement geo2 : h) {
			if (geo2 instanceof GeoLocusStroke) {
				this.penDeleteMode = true;
				return;
			}
		}
	}

	/**
	 * resets objDeleteMode and penDeleteMode on mouse press
	 */
	public void mousePressed() {
		this.objDeleteMode = false;
		this.penDeleteMode = false;
	}
}
