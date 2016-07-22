package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;

public class AdjustSlider {
	private GeoNumeric number;
	private EuclidianView view;
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

		Log.debug("[ADJUST] ratioX: " + ratioX + " ratioY: " + ratioY);
		x = number.getSliderX();
		origX = number.getOrigSliderX();

		y = number.getSliderY();
		origY = number.getOrigSliderY();

		width = number.getSliderWidth();
		origWidth = number.getOrigSliderWidth() == null ? 0
				: number.getOrigSliderWidth().doubleValue();

		horizontal = number.isSliderHorizontal();

	}

	private boolean isXOnScreen() {
		if (view == null || origX == null) {
			return false;
		}

		if (horizontal) {

			if (x == origX && origX + origWidth < view.getWidth()
					&& origWidth == width) {
				return true;
			}
		}
		return false;
	}

	private boolean isYOnScreen() {
		if (view == null || origY == null) {
			return false;
		}

		if (horizontal) {
			return false;
		}

		if (y == origY && origY < view.getHeight() && origWidth == width) {
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
		if (isXOnScreen() || isYOnScreen()) {
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

	// public void apply() {
	// if (!reduceWidth()) {
	// restoreWidth();
	// if (horizontal) {
	// Log.debug("[ADJUST] horizontal");
	// if (restoreX()) {
	// Log.debug("[ADJUST] sliderX is restored.");
	// } else if (isWidthOriginal() && !isXOnScreen()) {
	// x = view.getViewWidth() - width - MARGIN_X;
	// Log.debug("[ADJUST] x to the left");
	// }
	//
	// if (!restoreY() && origY != null
	// && origY > view.getViewHeight() - MARGIN_Y) {
	// y = view.getViewHeight() - MARGIN_Y;
	// }
	// } else {
	// // adjusting vertical slider
	// if (restoreY()) {
	// Log.debug("[ADJUST] sliderY is restored.");
	// } else if (isWidthOriginal() && !isYOnScreen()) {
	// y = view.getViewHeight() - MARGIN_Y;
	// Log.debug("[ADJUST] y to the bottom");
	// }
	//
	// if (!restoreX() && isWidthOriginal()
	// && origX + origWidth > view.getViewWidth() - MARGIN_X) {
	// x = view.getViewWidth() - MARGIN_X;
	// }
	// }
	//
	// }
	// number.setSliderLocation(x, y, true);
	// }


	private boolean isWidthOriginal() {
		return width == origWidth;
	}

	private boolean reduceWidth() {
		if (horizontal && width > view.getWidth()) {
			number.setSliderWidth(view.getViewWidth() - 2 * MARGIN_X);
			x = MARGIN_X;
				return true;

		} else if (!horizontal && width > view.getHeight()) {
			number.setSliderWidth(view.getViewHeight() - 2 * MARGIN_Y);
			y = view.getViewHeight() - MARGIN_Y;
			return true;
		}
		return false;
	}

	private void restoreWidth() {
		if (width < origWidth) {
			number.setSliderWidth(origWidth);
		}

	}

	private boolean restoreX() {
		if (origX != null && x != origX
				&& origX + width + MARGIN_X < view.getWidth()) {
			// x = Math.min(origX,
			// view.getViewWidth() - width - MARGIN_X);
			x = origX;
			return true;
		}
		return false;
	}

	private boolean restoreY() {
		if (origY != null && y != origY && origY < view.getHeight()) {
			y = origY;
			return true;
		}
		return false;
	}
}
