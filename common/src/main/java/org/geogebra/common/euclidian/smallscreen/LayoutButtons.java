package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.util.debug.Log;

public class LayoutButtons {
	private static final int Y_GAP = 5;
	private static final int X_GAP = 5;
	private List<GeoButton> all = new ArrayList<GeoButton>();
	private List<GeoButton> fixed = new ArrayList<GeoButton>();
	private List<GeoButton> moveable = new ArrayList<GeoButton>();
	private EuclidianView view;
	private ButtonComparator comparatorX = new ButtonComparator(false);
	private ButtonComparator comparatorY = new ButtonComparator(true);

	private boolean multiColumn;
	private int sumOfHeight;

	private static class ButtonComparator implements Comparator<GeoButton> {
		private boolean vertical;

		public ButtonComparator(boolean vertical) {
			this.vertical = vertical;
		}

		@Override
		public int compare(GeoButton o1, GeoButton o2) {
			if (vertical) {
				int y1 = o1.getAbsoluteScreenLocY();
				int y2 = o2.getAbsoluteScreenLocY();
				if (y1 == y2) {
					return 0;
				}
				return Kernel.isGreater(y2, y1) ? -1 : 1;
			}

			int x1 = o1.getAbsoluteScreenLocX();
			int x2 = o2.getAbsoluteScreenLocX();
			if (x1 == x2) {
				return 0;
			}
			return Kernel.isGreater(x2, x1) ? -1 : 1;

		}
	}

	public LayoutButtons(EuclidianView view) {
		this.view = view;
	}

	public void add(GeoButton button) {
		all.add(button);
	}

	public void clear() {
		all.clear();
		fixed.clear();
		moveable.clear();
	}

	protected void sortX() {
		Collections.sort(all, comparatorX);
	}

	protected void sortY() {
		Collections.sort(all, comparatorY);
	}

	private String buttonDetails(GeoButton btn) {
		return btn + " (" + btn.getAbsoluteScreenLocX() + ", "
				+ btn.getAbsoluteScreenLocY() + ")";
	}

	private void debugButtons(List<GeoButton> buttons) {
		for (int idx = 0; idx < buttons.size(); idx++) {
			GeoButton btn = buttons.get(idx);
			Log.debug("[LayoutButtons] " + idx + ". " + buttonDetails(btn));

		}

	}

	private GeoButton getLastFixed() {
		int size = fixed.size();
		if (size > 0) {
			return fixed.get(size - 1);
		}
		return null;
	}

	public void apply() {
		applyVertically();
		applyHorizontally();
	}

	private void applyVertically() {
		sortY();
		divideY();
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}

		if (multiColumn) {
			layoutInColumns();
			return;
		}

		int y = view.getHeight();

		GeoButton lastFixed = getLastFixed();
		int ySpace = view.getHeight() - (lastFixed == null ? Y_GAP
				: lastFixed.getAbsoluteScreenLocY() + lastFixed.getHeight()
						+ Y_GAP);

		int h = getHeights(moveable);

		while (ySpace < h && lastFixed != null) {
			moveable.add(0, lastFixed);
			fixed.remove(fixed.size() - 1);
			h += lastFixed.getHeight() + Y_GAP;
			lastFixed = getLastFixed();
			ySpace = view.getHeight() - (lastFixed == null ? Y_GAP
					: lastFixed.getAbsoluteScreenLocY() + lastFixed.getHeight()
							+ Y_GAP);
		}

		y = view.getHeight() - getHeights(moveable);
		for (GeoButton btn : moveable) {
			btn.setAbsoluteScreenLoc(btn.getAbsoluteScreenLocX(), y);
			y += btn.getHeight() + Y_GAP;
		}
	}

	private void applyHorizontally() {
		sortX();
		divideX();
		Log.debug("[LayoutButtons] applyHorizontally");
		debugButtons(fixed);
		debugButtons(moveable);
		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}

		int x = view.getWidth();

		GeoButton lastFixed = getLastFixed();
		int xSpace = view.getWidth() - (lastFixed == null ? X_GAP
				: lastFixed.getAbsoluteScreenLocX() + lastFixed.getWidth()
						+ X_GAP);

		int w = getWidths(moveable);

		while (xSpace < w && lastFixed != null) {
			moveable.add(0, lastFixed);
			fixed.remove(fixed.size() - 1);
			w += lastFixed.getWidth() + X_GAP;
			lastFixed = getLastFixed();
			xSpace = view.getWidth() - (lastFixed == null ? X_GAP
					: lastFixed.getAbsoluteScreenLocX() + lastFixed.getWidth()
							+ X_GAP);

		}

		x = view.getWidth() - getWidths(moveable);
		for (GeoButton btn : moveable) {
			btn.setAbsoluteScreenLoc(x, btn.getAbsoluteScreenLocY());
			x += btn.getWidth() + X_GAP;
		}
	}

	private void layoutInColumns() {
		// TODO Auto-generated method stub

	}

	private void divideX() {
		fixed.clear();
		moveable.clear();
		int idx = 0;
		while (idx < all.size()) {
			GeoButton btn = all.get(idx);

			if (isHorizontallyOnScreen(btn)) {
				fixed.add(btn);
			} else {
				moveable.add(btn);
			}
			idx++;
		}

	}

	private void divideY() {
		fixed.clear();
		moveable.clear();
		multiColumn = false;
		sumOfHeight = 0;
		int idx = 0;
		while (!multiColumn && idx < all.size()) {
			GeoButton btn = all.get(idx);
			sumOfHeight += btn.getHeight() + Y_GAP;
			multiColumn = view.getHeight() < sumOfHeight;
			if (isVerticallyOnScreen(btn)) {
				fixed.add(btn);
			} else {
				moveable.add(btn);
			}
			idx++;
		}

	}

	private static int getWidths(List<GeoButton> buttons) {
		int w = 0;
		for (GeoButton btn : buttons) {
			w += btn.getWidth() + X_GAP;
		}
		return w;
	}

	private static int getHeights(List<GeoButton> buttons) {
		int h = 0;
		for (GeoButton btn : buttons) {
			h += btn.getHeight() + Y_GAP;
		}
		return h;
	}

	private boolean isHorizontallyOnScreen(GeoButton btn) {
		int x = btn.getAbsoluteScreenLocX();
		int width = btn.getWidth();
		return x + width < view.getViewWidth();
	}

	private boolean isVerticallyOnScreen(GeoButton btn) {
		int y = btn.getAbsoluteScreenLocY();
		int height = btn.getHeight();
		return y + height < view.getViewHeight();
	}

}

