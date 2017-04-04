package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.ScreenLocation;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class LayoutAbsoluteGeos {
	private static final int Y_GAP = 5;
	private static final int X_GAP = 5;
	private List<AbsoluteScreenLocateable> originals = new ArrayList<AbsoluteScreenLocateable>();
	private List<AbsoluteScreenLocateable> all = new ArrayList<AbsoluteScreenLocateable>();
	private List<AbsoluteScreenLocateable> moveable = new ArrayList<AbsoluteScreenLocateable>();
	private final EuclidianView view;
	private final static AbsoluteGeoComparator comparatorX = new AbsoluteGeoComparator(
			false);
	private final static AbsoluteGeoComparator comparatorY = new AbsoluteGeoComparator(
			true);

	// Buttons should be collected only once.
	private boolean collected = false;

	private static class AbsoluteGeoComparator
			implements Comparator<AbsoluteScreenLocateable> {
		private boolean vertical;

		public AbsoluteGeoComparator(boolean vertical) {
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
				return Kernel.isGreater(y1, y2) ? -1 : 1;
			}

			int x1 = o1.getAbsoluteScreenLocX();
			int x2 = o2.getAbsoluteScreenLocX();
			if (x1 == x2) {
				return 0;
			}
			return Kernel.isGreater(x1, x2) ? -1 : 1;

		}
	}

	public LayoutAbsoluteGeos(EuclidianView view) {
		this.view = view;
	}

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
		Log.debug("[LayoutAbsoluteGeos] reset ");
		for (AbsoluteScreenLocateable loc : originals) {
			ScreenLocation screenLoc = ((GeoElement) loc).getScreenLocation();
			if (screenLoc != null) {
				loc.setAbsoluteScreenLoc(screenLoc.getX(), screenLoc.getY());
			}
		}
	}

	public void clear() {
		all.clear();
		moveable.clear();
	}

	public void restart() {
		clear();
		collected = false;
		originals.clear();
	}

	protected void sortX() {
		Collections.sort(all, comparatorX);
	}

	protected void sortY() {
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

		ArrayList<GRectangle> usedPositions = new ArrayList<GRectangle>();
		for (AbsoluteScreenLocateable absGeo : moveable) {
			final int x = absGeo.getAbsoluteScreenLocX();
			int y = maxUnusedY(usedPositions, x, x + absGeo.getTotalWidth(view),
					view.getHeight());
			y -= absGeo.getTotalHeight(view) + Y_GAP;
			y = Math.min(absGeo.getAbsoluteScreenLocY(), y);
			setAbsoluteScreenLoc(absGeo, x, y);
			usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
					absGeo.getTotalWidth(view), absGeo.getTotalHeight(view)));
			absGeo.update();
		}
	}

	private void applyHorizontally() {
		sortX();
		divideX();
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}


		ArrayList<GRectangle> usedPositions = new ArrayList<GRectangle>();
		for (AbsoluteScreenLocateable absGeo : moveable) {
			final int y = absGeo.getAbsoluteScreenLocY();
			int x = maxUnusedX(usedPositions, y,
					y + absGeo.getTotalHeight(view),
					view.getWidth());
			x -= absGeo.getTotalWidth(view) + X_GAP;
			x = Math.min(absGeo.getAbsoluteScreenLocX(), x);
			setAbsoluteScreenLoc(absGeo, x, y);
			usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
					absGeo.getTotalWidth(view), absGeo.getTotalHeight(view)));
			absGeo.update();

		}
	}


	private void setAbsoluteScreenLoc(AbsoluteScreenLocateable absGeo, int x,
			int y) {
		if (absGeo instanceof GeoBoolean) {
			((GeoBoolean) absGeo).setAbsoluteScreenLoc(x, y, true);
		} else {
			absGeo.setAbsoluteScreenLoc(x, y);
		}

	}

	private int maxUnusedX(ArrayList<GRectangle> usedPositions, int yTop, int yBottom, int max0) {
		int max = max0;
		for(GRectangle rect: usedPositions){
			if (MyMath.intervalsIntersect(rect.getMinY(), rect.getMaxY(), yTop,
					yBottom)) {
				if (max > rect.getMinX()) {
					max = (int) rect.getMinX();
				}
			}
		}
		return max;
	}

	private int maxUnusedY(ArrayList<GRectangle> usedPositions, int xLeft,
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
		Log.debug("Checking" + view.getSettings().getFileWidth());
		return view.getSettings().getFileWidth() == 0
				|| x + width < view.getSettings().getFileWidth();
	}

	private boolean isVerticallyOnScreen(AbsoluteScreenLocateable absGeo) {
		int y = absGeo.getAbsoluteScreenLocY();
		int height = absGeo.getTotalHeight(view);
		return view.getSettings().getFileHeight() == 0
				|| y + height < view.getSettings().getFileHeight();
	}

	// private static int getTotalWidths(List<GeoButton> buttons) {
	// int w = 0;
	// for (GeoButton absGeo : buttons) {
	// w += absGeo.getTotalWidth(view) + X_GAP;
	// }
	// return w;
	// }

	// private static int getHeights(List<GeoButton> buttons) {
	// int h = 0;
	// for (GeoButton absGeo : buttons) {
	// h += absGeo.getHeight() + Y_GAP;
	// }
	// return h;
	// }

	public boolean isCollected() {
		return collected;
	}

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
	public static boolean match(GeoElement geo) {
		// TODO Auto-generated method stub
		return geo.isGeoButton() || (geo.isGeoBoolean()
				&& geo.isEuclidianShowable()
				|| ((geo.isGeoText() || geo.isGeoImage()) && geo.isVisible())
				|| (geo.isGeoList() && ((GeoList) geo).drawAsComboBox())
				|| (geo.isGeoImage() && ((AbsoluteScreenLocateable) geo)
						.isAbsoluteScreenLocActive())
		);
	}

}

