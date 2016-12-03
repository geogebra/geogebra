package org.geogebra.common.euclidian.smallscreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawInputBox;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
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
	private static final int HSLIDER_OVERLAP_THRESOLD = 25;
	private static final int VSLIDER_OVERLAP_THRESOLD = 50;
	private static final int BUTTON_GAP = 10;
	private EuclidianView view;
	private App app;
	private Kernel kernel;
	private boolean enabled;
	private List<GeoNumeric> hSliders = new ArrayList<GeoNumeric>();
	private List<GeoNumeric> vSliders = new ArrayList<GeoNumeric>();
	private List<GeoButton> buttons = new ArrayList<GeoButton>();
	private List<GeoInputBox> inputBoxes = new ArrayList<GeoInputBox>();

	private static class HSliderComparator implements Comparator<GeoNumeric> {
		public HSliderComparator() {
			// TODO Auto-generated constructor stub
		}

		public int compare(GeoNumeric o1, GeoNumeric o2) {
			double y1 = o1.getSliderY();
			double y2 = o2.getSliderY();
			if (y1 == y2) {
				return 0;
			}
			return y1 < y2 ? -1 : 1;
		}
	}

	private static class VSliderComparator implements Comparator<GeoNumeric> {
		public VSliderComparator() {
			// TODO Auto-generated constructor stub
		}

		public int compare(GeoNumeric o1, GeoNumeric o2) {
			double x1 = o1.getSliderX();
			double x2 = o2.getSliderX();
			if (x1 == x2) {
				return 0;
			}
			return x1 < x2 ? -1 : 1;
		}
	}

	private static class ButtonComparator implements Comparator<GeoButton> {
		public ButtonComparator() {
			// TODO Auto-generated constructor stub
		}

		public int compare(GeoButton o1, GeoButton o2) {
			int y1 = o1.getAbsoluteScreenLocY();
			int y2 = o2.getAbsoluteScreenLocY();
			if (y1 == y2) {
				return 0;
			}
			return y1 < y2 ? -1 : 1;
		}
	}

	/**
	 * @param view
	 *            view
	 */
	public AdjustScreen(EuclidianView view) {
		this.view = view;
		app = view.getApplication();
		kernel = app.getKernel();
		enabled = true;// needsAdjusting();
	}

	/**
	 * Collect widgets, ensures they are on the screen and does not overlap.
	 */
	public void apply() {
		if (!enabled) {
			return;
		}

		collectWidgets();
		checkOvelappingHSliders();
		checkOvelappingVSliders();
		checkOvelappingButtons();
		checkOvelappingInputs();
		view.repaintView();
	}


	private void collectWidgets() {
		hSliders.clear();
		vSliders.clear();
		buttons.clear();
		inputBoxes.clear();

		Log.debug("[AS] collectWidgets()");
		for (GeoElement geo : kernel.getConstruction().getGeoTable().values()) {
			boolean ensure = false;
			if (geo instanceof GeoNumeric) {
				GeoNumeric num = (GeoNumeric) geo;
				if (num.isSlider()) {
					if (num.isSliderHorizontal()) {
						hSliders.add(num);
					} else {
						vSliders.add(num);
					}
					ensure = true;
				}
			} else if (geo.isGeoButton()) {
				if (geo.isGeoInputBox()) {
					Log.debug("[AS] collecting inputbox: " + geo);
					GeoInputBox input = (GeoInputBox) geo;
					inputBoxes.add(input);
				} else {
					Log.debug("[AS] collecting buttons: " + geo);
					GeoButton btn = (GeoButton) geo;
					buttons.add(btn);
				}
				ensure = true;
			}

			if (ensure) {
				ensureGeoOnScreen(geo);
			}
		}

	}

	private void ensureGeoOnScreen(GeoElement geo) {
		if (!app.has(Feature.ADJUST_WIDGETS)) {
			return;
		}
		Log.debug("[AS]  ensureGeoOnScreen geo: " + geo);

		AdjustWidget adjust = null;
		if (geo.isGeoNumeric()) {
			GeoNumeric number = (GeoNumeric) geo;
			if (number.isSlider()) {
				adjust = new AdjustSlider(number, view);
			}
		} else if (geo.isGeoButton()) {
			if (geo.isGeoInputBox()) {
				adjust = new AdjustInputBox((GeoInputBox) geo, view);
			} else {
				Log.debug("[AS] AdjustButton for " + geo);
				adjust = new AdjustButton((GeoButton) geo, view);
			}
		}

		if (adjust != null) {
			adjust.apply();
			view.update(geo);
		}
	}
	private void checkOvelappingHSliders() {
		Collections.sort(hSliders, new HSliderComparator());
		for (int idx = 0; idx < hSliders.size() - 1; idx++) {
			GeoNumeric slider1 = hSliders.get(idx);
			GeoNumeric slider2 = hSliders.get(idx + 1);
			Log.debug("[AS] :" + slider1 + " - " + slider2);

			double y1 = slider1.getSliderY();
			double x2 = slider2.getSliderX();
			double y2 = slider2.getSliderY();
			if (y2 - y1 < HSLIDER_OVERLAP_THRESOLD) {
				Log.debug("[AS] HSLIDER adjusting " + slider2 + " to (" + x2
						+ ", " + (y1 + HSLIDER_OVERLAP_THRESOLD) + ")");
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
			Log.debug("[AS] VSIDER:" + slider1 + " - " + slider2);
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

	private void checkOvelappingButtons() {
		// No buttons at all.
		if (buttons.size() < 1) {
			return;
		}

		Collections.sort(buttons, new ButtonComparator());
		Log.debug("[AS] Buttons:");
		int overlapCount = 0;
		for (int idx = 0; idx < buttons.size() - 1; idx++) {
			GeoButton btn1 = buttons.get(idx);
			GeoButton btn2 = buttons.get(idx + 1);
			GRectangle rect1 = AwtFactory.getPrototype().newRectangle(btn1.getAbsoluteScreenLocX(),
					btn1.getAbsoluteScreenLocY(),
					btn1.getWidth(), btn1.getHeight());
			GRectangle rect2 = AwtFactory.getPrototype().newRectangle(
					btn2.getAbsoluteScreenLocX(), btn2.getAbsoluteScreenLocY(),
					btn2.getWidth(), btn2.getHeight());
			
			boolean overlap = rect1.intersects(rect2)
					|| rect2.intersects(rect1);
			
			Log.debug("[AS] " + btn1);// + " - " + btn2 + " overlaps: " +
										// overlap);

			if (overlap) {
				overlapCount++;
				btn2.setAbsoluteScreenLoc(btn2.getAbsoluteScreenLocX(),
						btn1.getAbsoluteScreenLocY()
								+ overlapCount * btn1.getHeight()
								+ BUTTON_GAP);

				buttons.set(idx + 1, btn2);
				btn2.update();
			}
		}

		tileButtons(buttons);


	}

	private void tileButtons(List<? extends GeoButton> buttonList) {
		if (buttonList.size() < 1) {
			return;
		}
		Collections.sort(buttonList, new ButtonComparator());

		GeoButton last = buttonList.get(buttonList.size() - 1);
		int lastBottom = last.getAbsoluteScreenLocY() + last.getHeight();

		int maxBottom = view.getViewHeight() - AdjustButton.MARGIN_Y;

		Log.debug("[TILE] last: " + last.getLabelSimple() + " bottom: "
				+ lastBottom + " maxBottom: " + maxBottom);
		if (lastBottom >= maxBottom) {
			double dY = lastBottom - maxBottom;
			for (int idx = 0; idx < buttonList.size(); idx++) {
				GeoButton btn = buttonList.get(idx);
				btn.setAbsoluteScreenLoc(btn.getAbsoluteScreenLocX(),
						(int) (btn.getAbsoluteScreenLocY() - dY));
				btn.update();
				Log.debug("[TILE] " + btn.getLabelSimple() + " updatedt to  ("
						+ btn.getAbsoluteScreenLocX() + ", "
						+ btn.getAbsoluteScreenLocY() + ")");

			}
		}
	}

	private void checkOvelappingInputs() {
		Collections.sort(inputBoxes, new ButtonComparator());
		Log.debug("[AS] inputs:");
		for (int idx = 0; idx < inputBoxes.size() - 1; idx++) {
			GeoInputBox input1 = inputBoxes.get(idx);
			GeoInputBox input2 = inputBoxes.get(idx + 1);
			DrawInputBox d1 = (DrawInputBox) view.getDrawableFor(input1);
			DrawInputBox d2 = (DrawInputBox) view.getDrawableFor(input2);
			if (d1 != null && d2 != null) {
				GDimension t1 = d1.getTotalSize();
				GDimension t2 = d2.getTotalSize();

				GRectangle rect1 = AwtFactory.getPrototype().newRectangle(
						input1.getAbsoluteScreenLocX(),
						input1.getAbsoluteScreenLocY(), t1.getWidth(),
						t1.getHeight());
				GRectangle rect2 = AwtFactory.getPrototype().newRectangle(
						input2.getAbsoluteScreenLocX(),
						input2.getAbsoluteScreenLocY(), t2.getWidth(),
						t2.getHeight());

				boolean overlap = rect1.intersects(rect2)
						|| rect2.intersects(rect1);

				Log.debug("[AS] " + input1 + " - " + input2 + " overlaps: "
						+ overlap);

				if (overlap) {
					input2.setAbsoluteScreenLoc(input2.getAbsoluteScreenLocX(),
							input1.getAbsoluteScreenLocY() + t1.getHeight()
									+ BUTTON_GAP);
					input2.update();
				}
			}
		}

		tileButtons(inputBoxes);

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

		Log.debug("[AS] file: " + fileWidth + "x" + fileHeight);

		if (!viewApp.has(Feature.ADJUST_WIDGETS) || fileWidth == 0
				|| fileHeight == 0) {
			return false;
		}

		double w = viewApp.getWidth();
		double h = viewApp.getHeight();
		Log.debug("[AS] app: " + w + "x" + h);
		if ((w == fileWidth && h == fileHeight) || w == 0 || h == 0) {
			return false;
		}

		return true;
	}
}
