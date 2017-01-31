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
	private List<GeoButton> all = new ArrayList<GeoButton>();
	private List<GeoButton> fixed = new ArrayList<GeoButton>();
	private List<GeoButton> moveable = new ArrayList<GeoButton>();
	private EuclidianView view;
	private ButtonComparator comparator = new ButtonComparator();
	private boolean multiColumn;
	private int sumOfHeight;

	private static class ButtonComparator implements Comparator<GeoButton> {
		public ButtonComparator() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public int compare(GeoButton o1, GeoButton o2) {
			int y1 = o1.getAbsoluteScreenLocY();
			int y2 = o2.getAbsoluteScreenLocY();
			if (y1 == y2) {
				return 0;
			}
			return Kernel.isGreater(y2, y1) ? -1 : 1;
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

	protected void sort() {
		Collections.sort(all, comparator);
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
		sort();
		divide();
		// Log.debug("[LayoutButtons] after divide:");
		// Log.debug("[LayoutButtons] fixeds");
		// debugButtons(fixed);
		//
		// Log.debug("[LayoutButtons] moveables");
		// debugButtons(moveable);
		// Log.debug("[LayoutButtons] --------------------------");

		if (moveable.isEmpty()) {
			// All buttons is on screen, nothing to do.
			return;
		}

		if (multiColumn) {
			layoutInColumns();
			return;
		}

		int y = view.getHeight();

		// for (GeoButton btn : moveable) {
		// btn.setAbsoluteScreenLoc(btn.getAbsoluteScreenLocX() - 100, y);
		// y += btn.getHeight() + Y_GAP;
		// }

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

		// Log.debug("[LayoutButtons] after apply:");
		// Log.debug("[LayoutButtons] fixeds");
		// debugButtons(fixed);
		//
		// Log.debug("[LayoutButtons] moveables");
		// debugButtons(moveable);
		// Log.debug("[LayoutButtons] ==========================");

		y = view.getHeight() - getHeights(moveable);
		for (GeoButton btn : moveable) {
			btn.setAbsoluteScreenLoc(btn.getAbsoluteScreenLocX(), y);
			y += btn.getHeight() + Y_GAP;
		}
	}

	private void layoutInColumns() {
		// TODO Auto-generated method stub

	}

	private void divide() {
		multiColumn = false;
		sumOfHeight = 0;
		int idx = 0;
		while (!multiColumn && idx < all.size()) {
			GeoButton btn = all.get(idx);
			sumOfHeight += btn.getHeight() + Y_GAP;
			multiColumn = view.getHeight() < sumOfHeight;
			if (AdjustButton.isVerticallyOnScreen(btn, view)) {
				fixed.add(btn);
			} else {
				moveable.add(btn);
			}
			idx++;
		}

	}

	private int getHeights(List<GeoButton> buttons) {
		int h = 0;
		for (GeoButton btn : buttons) {
			h += btn.getHeight() + Y_GAP;
		}
		return h;
	}
}

