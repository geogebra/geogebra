package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.AbsoluteScreenLocateable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.util.DoubleUtil;

/**
 * Checks if the original screen was bigger when file was saved or not. If so,
 * some widgets needs to be adjusted to fit the smaller screen.
 * 
 * @author laszlo
 *
 */
public class AdjustScreen {
	private static final int HSLIDER_OVERLAP_THRESOLD = 25;
	private static final int VSLIDER_OVERLAP_THRESOLD = 50;
	// private static final int BUTTON_Y_GAP = 10;
	private EuclidianView view;
	private App app;
	private Kernel kernel;
	private boolean enabled;
	private List<GeoNumeric> hSliders = new ArrayList<>();
	private List<GeoNumeric> vSliders = new ArrayList<>();
	private List<GeoInputBox> inputBoxes = new ArrayList<>();
	private LayoutAbsoluteGeos layoutAbsoluteGeos;

	private static class HSliderComparator implements Comparator<GeoNumeric> {
		protected HSliderComparator() {
			// avoid synthetic access warning
		}

		@Override
		public int compare(GeoNumeric o1, GeoNumeric o2) {
			double y1 = o1.getSliderY();
			double y2 = o2.getSliderY();
			if (y1 == y2) {
				return 0;
			}
			return DoubleUtil.isGreater(y2, y1) ? -1 : 1;
		}
	}

	private static class VSliderComparator implements Comparator<GeoNumeric> {
		protected VSliderComparator() {
			// avoid synthetic access warning
		}

		@Override
		public int compare(GeoNumeric o1, GeoNumeric o2) {
			double x1 = o1.getSliderX();
			double x2 = o2.getSliderX();
			if (x1 == x2) {
				return 0;
			}
			return DoubleUtil.isGreater(x2, x1) ? -1 : 1;
		}
	}

	// private static class ButtonComparator implements Comparator<GeoButton> {
	// public ButtonComparator() {
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public int compare(GeoButton o1, GeoButton o2) {
	// int y1 = o1.getAbsoluteScreenLocY();
	// int y2 = o2.getAbsoluteScreenLocY();
	// if (y1 == y2) {
	// return 0;
	// }
	// return Kernel.isGreater(y1, y2) ? 1 : -1;
	// }
	// }

	/**
	 * @param view
	 *            view
	 */
	public AdjustScreen(EuclidianView view) {
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		enabled = true; // needsAdjusting();
		layoutAbsoluteGeos = new LayoutAbsoluteGeos(view);
	}

	/**
	 * Remove old collection of buttons
	 */
	public void restartButtons() {
		layoutAbsoluteGeos.restart();
	}

	/**
	 * Collect widgets, ensures they are on the screen and does not overlap.
	 * 
	 * @param reset
	 *            whether to reset buttons from original coords
	 */
	public void apply(boolean reset) {
		if (!enabled) {
			return;
		}

		if (reset) {
			reset();
		} else {
			collectWidgets();
		}

		checkOvelappingHSliders();
		checkOvelappingVSliders();
		layoutAbsoluteGeos.apply();
		// checkOvelappingInputs();
		view.repaintView();
	}

	private void collectWidgets() {
		hSliders.clear();
		vSliders.clear();
		layoutAbsoluteGeos.clear();
		inputBoxes.clear();

		for (GeoElement geo : kernel.getConstruction().getGeoTable().values()) {
			if (geo instanceof GeoNumeric) {
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isSlider()) {
					if (num.isSliderHorizontal()) {
						hSliders.add(num);
					} else {
						vSliders.add(num);
					}
				}
			} else if (layoutAbsoluteGeos.match(geo)) {
				if (!layoutAbsoluteGeos.isCollected()) {
					AbsoluteScreenLocateable absGeo = (AbsoluteScreenLocateable) geo;
					layoutAbsoluteGeos.add(absGeo);
				}
			}
		}
		layoutAbsoluteGeos.setCollected(true);
	}

	/**
	 * Reset buttons from original coords
	 */
	public void reset() {
		layoutAbsoluteGeos.reset();
	}

	private void checkOvelappingHSliders() {
		Collections.sort(hSliders, new HSliderComparator());
		for (int idx = 0; idx < hSliders.size() - 1; idx++) {
			GeoNumeric slider1 = hSliders.get(idx);
			GeoNumeric slider2 = hSliders.get(idx + 1);

			double y1 = slider1.getSliderY();
			double x2 = slider2.getSliderX();
			double y2 = slider2.getSliderY();
			if (y2 - y1 < HSLIDER_OVERLAP_THRESOLD) {
				slider2.setSliderLocation(x2, y1 + HSLIDER_OVERLAP_THRESOLD,
						true);
				slider2.update();
			}
		}
		if (hSliders.size() < 1) {
			return;
		}
		GeoNumeric lastSlider = hSliders.get(hSliders.size() - 1);
		int maxY = view.getViewHeight() - AdjustSlider.MARGIN_Y;
		if (lastSlider.getSliderY() > maxY) {
			double dY = lastSlider.getSliderY() - maxY;
			for (int idx = 0; idx < hSliders.size(); idx++) {
				GeoNumeric slider = hSliders.get(idx);
				slider.setSliderLocation(slider.getSliderX(),
						slider.getSliderY() - dY, true);
			}
		}
	}

	private void checkOvelappingVSliders() {
		Collections.sort(vSliders, new VSliderComparator());
		for (int idx = 0; idx < vSliders.size() - 1; idx++) {
			GeoNumeric slider1 = vSliders.get(idx);
			GeoNumeric slider2 = vSliders.get(idx + 1);
			double x1 = slider1.getSliderX();
			double x2 = slider2.getSliderX();
			double y2 = slider2.getSliderY();
			if (x2 - x1 < VSLIDER_OVERLAP_THRESOLD) {
				slider2.setSliderLocation(x1 + VSLIDER_OVERLAP_THRESOLD, y2,
						true);
				slider2.update();
			}
		}
	}

	/**
	 * @return if the original screen was bigger so adjusting is needed.
	 */
	protected boolean needsAdjusting() {
		App viewApp = view.getApplication();
		int fileWidth = viewApp.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = viewApp.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		if (fileWidth == 0 || fileHeight == 0) {
			return false;
		}

		double w = viewApp.getWidth();
		double h = viewApp.getHeight();
		return !(w == fileWidth && h == fileHeight) && (w != 0) && (h != 0);
	}

	/**
	 * Adjust the coordinate system to the screen size
	 * 
	 * @param view
	 *            {@link EuclidianView}
	 */
	public static void adjustCoordSystem(EuclidianView view) {
		EuclidianSettings s = view.getSettings();
		double rX = (double) view.getWidth() / s.getFileWidth();
		double rY = (double) view.getHeight() / s.getFileHeight();

		double ox = s.getFileXZero() * rX;
		double oy = s.getFileYZero() * rY;
		double scale = s.getFileYScale() * rY;
		view.setCoordSystem(ox, oy, scale, scale);
	}
}
