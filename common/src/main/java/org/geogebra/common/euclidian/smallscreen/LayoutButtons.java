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
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

public class LayoutButtons {
	private static final int Y_GAP = 5;
	private static final int X_GAP = 5;
	private List<AbsoluteScreenLocateable> originals = new ArrayList<AbsoluteScreenLocateable>();
	private List<AbsoluteScreenLocateable> all = new ArrayList<AbsoluteScreenLocateable>();
	private List<AbsoluteScreenLocateable> moveable = new ArrayList<AbsoluteScreenLocateable>();
	private EuclidianView view;
	private final static ButtonComparator comparatorX = new ButtonComparator(
			false);
	private final static ButtonComparator comparatorY = new ButtonComparator(
			true);

	// Buttons should be collected only once.
	private boolean collected = false;

	private static class ButtonComparator
			implements Comparator<AbsoluteScreenLocateable> {
		private boolean vertical;

		public ButtonComparator(boolean vertical) {
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

	public LayoutButtons(EuclidianView view) {
		this.view = view;
	}

	public void add(AbsoluteScreenLocateable button) {
		originals.add(button);
	}

	public void reset() {
		Log.debug("[LayoutButtons] reset ");
		for (AbsoluteScreenLocateable loc : originals) {
			if (loc instanceof GeoButton) {
				GeoButton btn = (GeoButton) loc;
				if (btn.getOrigX() != null && btn.getOrigY() != null) {
					btn.setAbsoluteScreenLoc(btn.getOrigX(), btn.getOrigY());

				}
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

	private static String buttonDetails(AbsoluteScreenLocateable btn,
			EuclidianView view) {
		return btn + " (" + btn.getAbsoluteScreenLocX() + ", "
				+ btn.getAbsoluteScreenLocY() + "), " + btn.getTotalWidth(view)
				+ "x"
				+ btn.getTotalHeight(view);
	}

	private void debugButtons(String msg,
			List<AbsoluteScreenLocateable> buttons) {
		Log.debug("[LayoutButtons] " + msg);
		for (int idx = 0; idx < buttons.size(); idx++) {
			AbsoluteScreenLocateable btn = buttons.get(idx);
			Log.debug(
					"[LayoutButtons] " + idx + ". " + buttonDetails(btn, view));

		}

	}



	public void apply() {
		all.clear();
		all.addAll(originals);

		debugButtons("initial: ", all);

		applyHorizontally();

		debugButtons("after horizontally: ", all);

		applyVertically();

		debugButtons("after vertically: ", all);

	}


	private void applyVertically() {
		sortY();
		divideY();
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}





		ArrayList<GRectangle> usedPositions = new ArrayList<GRectangle>();
		for (AbsoluteScreenLocateable btn : moveable) {
			final int x = btn.getAbsoluteScreenLocX();
			int y = maxUnusedY(usedPositions, x, x + btn.getTotalWidth(view),
					view.getHeight());
			y -= btn.getTotalHeight(view) + Y_GAP;
			y = Math.min(btn.getAbsoluteScreenLocY(), y);
			setAbsoluteScreenLoc(btn, x, y);
			usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
					btn.getTotalWidth(view), btn.getTotalHeight(view)));
			view.update(btn.toGeoElement());
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
		for (AbsoluteScreenLocateable btn : moveable) {
			final int y = btn.getAbsoluteScreenLocY();
			int x = maxUnusedX(usedPositions, y, y + btn.getTotalHeight(view),
					view.getWidth());
			x -= btn.getTotalWidth(view) + X_GAP;
			x = Math.min(btn.getAbsoluteScreenLocX(), x);
			setAbsoluteScreenLoc(btn, x, y);
			usedPositions.add(AwtFactory.getPrototype().newRectangle(x, y,
					btn.getTotalWidth(view), btn.getTotalHeight(view)));
			Log.debug(btn + "---->" + x);
			view.update(btn.toGeoElement());
		}
	}


	private void setAbsoluteScreenLoc(AbsoluteScreenLocateable btn, int x,
			int y) {
		if (btn instanceof GeoBoolean) {
			((GeoBoolean) btn).setAbsoluteScreenLoc(x, y, true);
		} else {
			btn.setAbsoluteScreenLoc(x, y);
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
		for (AbsoluteScreenLocateable btn : all) {
			if (isVerticallyOnScreen(btn)) {
				moveable.add(btn);
			}
		}

	}

	private void divideY() {
		moveable.clear();
		// sumOfHeight = 0;
		for (AbsoluteScreenLocateable btn : all) {
			if (isVerticallyOnScreen(btn)) {
				moveable.add(btn);
			}

		}

	}

	private boolean isHorizontallyOnScreen(AbsoluteScreenLocateable btn) {
		int x = btn.getAbsoluteScreenLocX();
		int width = btn.getTotalWidth(view);
		return x + width < view.getViewWidth();
	}

	private boolean isVerticallyOnScreen(AbsoluteScreenLocateable btn) {
		int y = btn.getAbsoluteScreenLocY();
		int height = btn.getTotalHeight(view);
		return y + height < view.getViewHeight();
	}

	// private static int getTotalWidths(List<GeoButton> buttons) {
	// int w = 0;
	// for (GeoButton btn : buttons) {
	// w += btn.getTotalWidth(view) + X_GAP;
	// }
	// return w;
	// }

	private static int getHeights(List<GeoButton> buttons) {
		int h = 0;
		for (GeoButton btn : buttons) {
			h += btn.getHeight() + Y_GAP;
		}
		return h;
	}

	public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		if (originals.isEmpty()) {
			return;
		}
		this.collected = collected;
	}

}

