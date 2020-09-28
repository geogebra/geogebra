package org.geogebra.common.euclidian.modes;

import java.util.Iterator;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
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
	private GRectangle rect = AwtFactory.getPrototype().newRectangle(0, 0, 100,
			100);

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
		rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
				eventY - ec.getDeleteToolSize() / 2,
				ec.getDeleteToolSize(), ec.getDeleteToolSize());

		view.setDeletionRectangle(rect);
		view.getHitDetector().setIntersectionHits(rect);
		Hits hits = view.getHits();
		if (!this.objDeleteMode && !this.penDeleteMode) {
			updatePenDeleteMode(hits);
		}
		boolean onlyStrokes = forceOnlyStrokes || this.penDeleteMode;

		// hide cursor, the new "cursor" is the deletion rectangle
		view.setCursor(EuclidianCursor.TRANSPARENT);

		Iterator<GeoElement> it = hits.iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			// delete tool should delete the object for dragging
			// at whiteboard
			// see MOW-97
			if (view.getApplication().isWhiteboardActive()
					&& ec.getMode() == EuclidianConstants.MODE_DELETE) {
				removeOrSetUndefinedIfHasFixedDescendent(geo);
			} else if (geo instanceof GeoLocusStroke) {
				boolean hasVisiblePart = deletePartOfPenStroke((GeoLocusStroke) geo);

				if (hasVisiblePart) { // still something visible, don't delete
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
		// do not delete images using eraser
		hits.removeImages();
		for (GeoElement hit : hits) {
			removeOrSetUndefinedIfHasFixedDescendent(hit);
		}
	}

	private void removeOrSetUndefinedIfHasFixedDescendent(GeoElement geo) {
		if (!view.getApplication().isApplet() || !geo.isLockedPosition()) {
			geo.removeOrSetUndefinedIfHasFixedDescendent();
		}
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
				rect.setBounds(eventX - ec.getDeleteToolSize() / 2,
						eventY - ec.getDeleteToolSize() / 2,
						ec.getDeleteToolSize(), ec.getDeleteToolSize());

				boolean hasVisiblePart = deletePartOfPenStroke((GeoLocusStroke) geos[0]);

				if (!hasVisiblePart) { // still something visible, don't delete
					// remove this Stroke
					removeOrSetUndefinedIfHasFixedDescendent(geos[0]);
				}
			} else if (!(geos[0] instanceof GeoImage)) { // delete this object
				removeOrSetUndefinedIfHasFixedDescendent(geos[0]);
			}
			return true;
		}
		return false;
	}

	private boolean deletePartOfPenStroke(GeoLocusStroke gls) {
		double x = view.toRealWorldCoordX(rect.getX());
		double y = view.toRealWorldCoordY(rect.getY() + rect.getHeight());
		double width = rect.getWidth() * view.getInvXscale();
		double height = rect.getHeight() * view.getInvYscale();

		GRectangle2D realRectangle = AwtFactory.getPrototype().newRectangle2D();
		realRectangle.setRect(x, y, width, height);

		return gls.deletePart(realRectangle);
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
