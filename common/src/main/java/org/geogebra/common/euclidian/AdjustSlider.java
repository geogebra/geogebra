package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

/**
 * Adjusts slider position on file load
 */
public class AdjustSlider {
	private GeoNumeric number;
	private final EuclidianView view;
	private double x;
	private double y;
	private Double origX;
	private Double origY;
	private double width;
	private double origWidth;
	private boolean horizontal;
	private double ratioX;
	private double ratioY;

	private static final int MARGIN_X = 15;
	private static final int MARGIN_Y = 15;

	/**
	 * @param num
	 *            slider
	 * @param view
	 *            view
	 */
	public AdjustSlider(GeoNumeric num, EuclidianView view) {
		this.number = num;
		this.view = view;

		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		ratioX = fileWidth == 0 ? 1 : (double) view.getViewWidth() / fileWidth;
		ratioY = fileHeight == 0 ? 1
				: (double) view.getViewHeight() / fileHeight;

//		Log.debug("[ADJUST] ratioX: " + ratioX + " ratioY: " + ratioY);
		x = number.getSliderX();
		origX = number.getOrigSliderX();

		y = number.getSliderY();
		origY = number.getOrigSliderY();

		width = number.getSliderWidth();
		origWidth = number.getOrigSliderWidth() == null ? 0
				: number.getOrigSliderWidth().doubleValue();

		horizontal = number.isSliderHorizontal();

	}

	private boolean isSliderOnScreen() {
		return horizontal ? isHSliderOnScreen() : isVSliderOnScreen();
	}

	private boolean isHSliderOnScreen() {
		if (origX == null) {
			return true;
		}

			if (x == origX && origX + origWidth < view.getWidth()
				&& origWidth == width && y == origY) {
				return true;
			}
		return false;
	}

	private boolean isVSliderOnScreen() {
		if (origY == null) {
			return true;
		}

		if (x == origX && origX < view.getWidth() && y == origY
				&& origY < view.getHeight()
				&& origWidth == width) {
			return true;
		}
		return false;
	}
	// private void restoreLocation() {
	//
	// number.setSliderWidth(origWidth);
	// number.setSliderLocation(origX, origY, true);
	// }

	/**
	 * Just do the job.
	 */
	public void apply() {
		if (isSliderOnScreen()) {
			return;
		}

		double ratio = horizontal ? ratioX : ratioY;

		if (ratio > 1) {
			return;
		}

		x = Math.round(origX * ratioX);
		y = Math.round(origY * ratioY);
		if (horizontal) {
			adjustToRight();
		} else {
			adjustToBottom();
		}

		if (width > view.getViewWidth() || width != origWidth) {
			width = Math.round(origWidth * ratio);
			number.setSliderWidth(width);
		}
		number.setSliderLocation(x, y, true);
	}

	private void adjustToRight() {
		if (x + width > view.getViewWidth()) {
			x = view.getViewWidth() - width;
		}
	}

	private void adjustToBottom() {
		if (y + width > view.getViewHeight()) {
			y = view.getViewHeight() - width;
		}
	}
}
