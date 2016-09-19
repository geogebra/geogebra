package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.util.debug.Log;

/**
 * Checks if the original screen was bigger when file was saved or not. If so,
 * some widgets needs to be adjusted to fit the smaller screen.
 * 
 * @author laszlo
 *
 */
public class AdjustScreen {
	private static final int HSLIDER_OVERLAP_THRESOLD = 50;
	private static final int VSLIDER_OVERLAP_THRESOLD = 50;
	private EuclidianView view;
	private App app;
	private Kernel kernel;
	private boolean enabled;
	private List<GeoNumeric> hSliders = new ArrayList<GeoNumeric>();
	private List<GeoNumeric> vSliders = new ArrayList<GeoNumeric>();

	private class HSliderComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			double y1 = ((GeoNumeric) o1).getSliderY();
			double y2 = ((GeoNumeric) o2).getSliderY();
			if (y1 == y2) {
				return 0;
			}
			return y1 < y2 ? -1 : 1;
		}
	}

	private class VSliderComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			double x1 = ((GeoNumeric) o1).getSliderX();
			double x2 = ((GeoNumeric) o2).getSliderX();
			if (x1 == x2) {
				return 0;
			}
			return x1 < x2 ? -1 : 1;
		}
	}

	public AdjustScreen(EuclidianView view) {
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		enabled = true;// needsAdjusting();
		apply();
	}

	private void apply() {
		if (!enabled) {
			return;
		}

		collectSliders();
	}

	private void collectSliders() {
		Log.debug("[AS] collectSliders()");
		for (GeoElement geo : kernel.getConstruction().getGeoTable().values()) {
			if (geo instanceof GeoNumeric) {
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isSlider()) {
					if (num.isSliderHorizontal()) {
						hSliders.add(num);
					} else {
						vSliders.add(num);
					}

					view.ensureGeoOnScreen(num);

				}
			}
		}

		checkOvelappingHSliders();
		checkOvelappingVSliders();
	}

	@SuppressWarnings("unchecked")
	private void checkOvelappingHSliders() {
		Collections.sort(hSliders, new HSliderComparator());
		int diff = HSLIDER_OVERLAP_THRESOLD;
		for (int idx = hSliders.size() - 1; idx > 0; idx--) {
			GeoNumeric num1 = hSliders.get(idx - 1);
			GeoNumeric num2 = hSliders.get(idx);
			Log.debug("[AS] :" + num1 + " - " + num2);
			// double x1 = num1.getSliderX();
			// double xEnd1 = num1.getSliderX() + num1.getSliderWidth();
			double y1 = num1.getSliderY();
			double x2 = num2.getSliderX();
			// double xEnd2 = num2.getSliderX() + num2.getSliderWidth();
			double y2 = num2.getSliderY();
			if (y2 - y1 < HSLIDER_OVERLAP_THRESOLD) {
				Log.debug("[AS] adjusting " + num2 + " to (" + x2 + ", "
						+ (y2 + diff) + ")");
				num2.setSliderLocation(x2, y2 + diff, true);
				diff += HSLIDER_OVERLAP_THRESOLD;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void checkOvelappingVSliders() {
		Collections.sort(vSliders, new VSliderComparator());
		int diff = VSLIDER_OVERLAP_THRESOLD;
		for (int idx = vSliders.size() - 1; idx > 0; idx--) {
			GeoNumeric num1 = vSliders.get(idx - 1);
			GeoNumeric num2 = vSliders.get(idx);
			Log.debug("[AS] :" + num1 + " - " + num2);
			double x1 = num1.getSliderX();
			// double xEnd1 = num1.getSliderX() + num1.getSliderWidth();
			double y1 = num1.getSliderY();
			double x2 = num2.getSliderX();
			// double xEnd2 = num2.getSliderX() + num2.getSliderWidth();
			double y2 = num2.getSliderY();
			if (x2 - x1 < VSLIDER_OVERLAP_THRESOLD) {
				Log.debug("[AS] adjusting " + num2 + " to (" + (x2 + diff)
						+ ", " + y2 + ")");
				num2.setSliderLocation(x2 + diff, y2, true);
				diff += VSLIDER_OVERLAP_THRESOLD;
			}
		}
	}

	/**
	 * @return if the original screen was bigger so adjusting is needed.
	 */
	protected boolean needsAdjusting() {
		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		Log.debug("[AS] file: " + fileWidth + "x" + fileHeight);

		if (!app.has(Feature.ADJUST_WIDGETS) || fileWidth == 0
				|| fileHeight == 0) {
			return false;
		}

		double w = app.getWidth();
		double h = app.getHeight();
		Log.debug("[AS] app: " + w + "x" + h);
		if ((w == fileWidth && h == fileHeight) || w == 0 || h == 0) {
			return false;
		}


		return true;
	}
}
