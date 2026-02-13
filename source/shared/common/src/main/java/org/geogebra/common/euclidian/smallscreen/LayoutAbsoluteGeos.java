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

package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenLocation;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

/**
 * Adjusts position of absolutely positioned elements (texts, buttons, lists,
 * images, checkboxes)
 * 
 * @author Laszlo
 *
 */
public class LayoutAbsoluteGeos {
	private static final int Y_GAP = 5;
	private static final int X_GAP = 5;
	private List<AbsoluteScreenLocateable> originals = new ArrayList<>();
	private List<AbsoluteScreenLocateable> all = new ArrayList<>();
	private List<AbsoluteScreenLocateable> moveable = new ArrayList<>();
	private final EuclidianView view;
	private final static AbsoluteGeoComparator comparatorX = new AbsoluteGeoComparator(
			false);
	private final static AbsoluteGeoComparator comparatorY = new AbsoluteGeoComparator(
			true);

	// Buttons should be collected only once.
	private boolean collected = false;

	private static final class AbsoluteGeoComparator
			implements Comparator<AbsoluteScreenLocateable> {
		private final boolean vertical;

		private AbsoluteGeoComparator(boolean vertical) {
			this.vertical = vertical;
		}

		@Override
		public int compare(AbsoluteScreenLocateable o1,
				AbsoluteScreenLocateable o2) {
			if (vertical) {
				int y1 = o1.getAbsoluteScreenLocY();
				int y2 = o2.getAbsoluteScreenLocY();
				if (y1 == y2) {
					return 0;
				}
				return DoubleUtil.isGreater(y1, y2) ? -1 : 1;
			}

			int x1 = o1.getAbsoluteScreenLocX();
			int x2 = o2.getAbsoluteScreenLocX();
			if (x1 == x2) {
				return 0;
			}
			return DoubleUtil.isGreater(x1, x2) ? -1 : 1;

		}
	}

	/**
	 * @param view
	 *            view
	 */
	public LayoutAbsoluteGeos(EuclidianView view) {
		this.view = view;
	}

	/**
	 * @param absGeo
	 *            absolute geo
	 */
	public void add(AbsoluteScreenLocateable absGeo) {
		if (view.isVisibleInThisView(absGeo)) {
			originals.add(absGeo);
		}
	}

	/**
	 * Reset all the collected geos to their original screen location that come
	 * from file.
	 */
	public void reset() {
		for (AbsoluteScreenLocateable loc : originals) {
			ScreenLocation screenLoc = ((GeoElement) loc).getScreenLocation();
			if (screenLoc != null) {
				loc.setAbsoluteScreenLoc(screenLoc.getX(), screenLoc.getY());
			}
		}
	}

	/**
	 * Clear the lists.
	 */
	public void clear() {
		all.clear();
		moveable.clear();
	}

	/**
	 * Clear the lists and prepare for collecting.
	 */
	public void restart() {
		clear();
		collected = false;
		originals.clear();
	}

	private void sortX() {
		Collections.sort(all, comparatorX);
	}

	private void sortY() {
		Collections.sort(all, comparatorY);
	}

	// private static String buttonDetails(AbsoluteScreenLocateable absGeo,
	// EuclidianView view) {
	// return absGeo + " (" + absGeo.getAbsoluteScreenLocX() + ", "
	// + absGeo.getAbsoluteScreenLocY() + "), " + absGeo.getTotalWidth(view)
	// + "x"
	// + absGeo.getTotalHeight(view);
	// }

	// private void debugButtons(String msg,
	// List<AbsoluteScreenLocateable> buttons) {
	// Log.debug("[LayoutButtons] " + msg);
	// for (int idx = 0; idx < buttons.size(); idx++) {
	// AbsoluteScreenLocateable absGeo = buttons.get(idx);
	// Log.debug(
	// "[LayoutButtons] " + idx + ". " + buttonDetails(absGeo, view));
	//
	// }
	//
	// }

	/**
	 * Adjust geos vertically and horizontally
	 */
	public void apply() {
		all.clear();
		all.addAll(originals);

		// debugButtons("initial: ", all);

		applyHorizontally();

		// debugButtons("after horizontally: ", all);

		applyVertically();

		// debugButtons("after vertically: ", all);

	}

	private void applyVertically() {
		sortY();
		divideY();
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}

		ArrayList<GRectangle> usedPositions = new ArrayList<>();
		boolean moveNeeded = false;
		for (AbsoluteScreenLocateable absGeo : moveable) {
			final int x = absGeo.getAbsoluteScreenLocX();
			int geoHeight = absGeo.getTotalHeight(view);
			int geoWidth = absGeo.getTotalWidth(view);
			int y = maxUnusedY(usedPositions, x, x + geoWidth,
					view.getHeight());
			y -= geoHeight + Y_GAP;
			int yCorner;
			if (bottomAnchor(absGeo)) {
				yCorner = y + geoHeight;
			} else {
				yCorner = y;
			}
			if (yCorner < absGeo.getAbsoluteScreenLocY() + Y_GAP) {
				moveNeeded = true;
			}
			if (moveNeeded) {
				y = Math.min(absGeo.getAbsoluteScreenLocY(), yCorner);
				setAbsoluteScreenLoc(absGeo, x, y);
				usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
						geoWidth, geoHeight));
				absGeo.update();
			}
		}
	}

	private static boolean bottomAnchor(AbsoluteScreenLocateable absGeo) {
		return absGeo.isGeoText() || absGeo.isGeoImage();
	}

	private void applyHorizontally() {
		sortX();
		divideX();
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}

		ArrayList<GRectangle> usedPositions = new ArrayList<>();
		boolean moveNeeded = false;
		for (AbsoluteScreenLocateable absGeo : moveable) {
			final int y = absGeo.getAbsoluteScreenLocY();
			int x = maxUnusedX(usedPositions, y,
					y + absGeo.getTotalHeight(view), view.getWidth());
			x -= absGeo.getTotalWidth(view) + X_GAP;
			if (x < absGeo.getAbsoluteScreenLocX() + X_GAP) {
				moveNeeded = true;
			}
			if (moveNeeded) {
				x = Math.min(absGeo.getAbsoluteScreenLocX(), x);
				setAbsoluteScreenLoc(absGeo, x, y);
				usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
						absGeo.getTotalWidth(view),
						absGeo.getTotalHeight(view)));
				absGeo.update();
			}

		}
	}

	private static void setAbsoluteScreenLoc(AbsoluteScreenLocateable absGeo,
			int x, int y) {
			absGeo.setAbsoluteScreenLoc(x, y);
	}

	private static int maxUnusedX(ArrayList<GRectangle> usedPositions, int yTop,
			int yBottom, int max0) {
		int max = max0;
		for (GRectangle rect : usedPositions) {
			if (MyMath.intervalsIntersect(rect.getMinY(), rect.getMaxY(), yTop,
					yBottom)) {
				if (max > rect.getMinX()) {
					max = (int) rect.getMinX();
				}
			}
		}
		return max;
	}

	private static int maxUnusedY(ArrayList<GRectangle> usedPositions,
			int xLeft,
			int xRight, int max0) {
		int max = max0;
		for (GRectangle rect : usedPositions) {
			if (MyMath.intervalsIntersect(rect.getMinX(), rect.getMaxX(), xLeft,
					xRight)) {
				if (max > rect.getMinY()) {
					max = (int) rect.getMinY();
				}
			}
		}
		return max;
	}

	private void divideX() {
		moveable.clear();
		for (AbsoluteScreenLocateable absGeo : all) {
			if (isHorizontallyOnScreen(absGeo)) {
				moveable.add(absGeo);
			}
		}

	}

	private void divideY() {
		moveable.clear();
		// sumOfHeight = 0;
		for (AbsoluteScreenLocateable absGeo : all) {
			if (isVerticallyOnScreen(absGeo)) {
				moveable.add(absGeo);
			}

		}

	}

	private boolean isHorizontallyOnScreen(AbsoluteScreenLocateable absGeo) {
		int x = absGeo.getAbsoluteScreenLocX();
		int width = absGeo.getTotalWidth(view);
		return view.getSettings().getFileWidth() == 0
				|| x + width < view.getSettings().getFileWidth();
	}

	private boolean isVerticallyOnScreen(AbsoluteScreenLocateable absGeo) {
		int y = absGeo.getAbsoluteScreenLocY();
		int height = absGeo.getTotalHeight(view);
		return view.getSettings().getFileHeight() == 0
				|| y + (bottomAnchor(absGeo) ? 0 : height) < view.getSettings()
						.getFileHeight();
	}

	/**
	 * @return whether widget collection was finished
	 */
	public boolean isCollected() {
		return collected;
	}

	/**
	 * @param collected
	 *            whether widget collection was finished
	 */
	public void setCollected(boolean collected) {
		if (originals.isEmpty()) {
			return;
		}
		this.collected = collected;
	}

	/**
	 * 
	 * @param geo
	 *            to check.
	 * @return if geo can be handled with this class or not.
	 */
	public boolean match(GeoElement geo) {
		if (!view.isVisibleInThisView(geo) || !geo.isEuclidianVisible()) {
			return false;
		}
		return geo.isGeoButton()
				|| (geo.isGeoBoolean() && geo.isEuclidianShowable())
				|| (geo.isGeoText() && geo.isVisible()
						&& ((AbsoluteScreenLocateable) geo)
								.isAbsoluteScreenLocActive())
				|| (geo.isGeoList() && ((GeoList) geo).drawAsComboBox())
				|| (geo.isGeoImage() && ((AbsoluteScreenLocateable) geo)
						.isAbsoluteScreenLocActive()
						&& !((GeoImage) geo).isInBackground());
	}

}
