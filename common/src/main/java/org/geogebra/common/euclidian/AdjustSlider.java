package org.geogebra.common.euclidian;

import org.geogebra.common.kernel.geos.GeoNumeric;

public class AdjustSlider {
	private GeoNumeric number;
	private EuclidianView view;
	private double x;
	private double y;
	private double origX;
	private double origY;
	private double width;
	private double origWidth;
	private boolean horizontal;

	private static final int MARGIN_X = 15;
	private static final int MARGIN_Y = 15;
	public AdjustSlider(GeoNumeric num, EuclidianView view) {
		this.number = num;
		this.view = view;

		x = number.getSliderX();
		if (number.getOrigSliderX() == null) {
			number.setOrigSliderX(x);
		}

		origX = number.getOrigSliderX();

		y = number.getSliderY();
		if (number.getOrigSliderY() == null) {
			number.setOrigSliderY(y);
		}

		origY = number.getOrigSliderY();

		width = number.getSliderWidth();
		origWidth = number.getOrigSliderWidth() == null ? 0
				: number.getOrigSliderWidth().doubleValue();

		horizontal = number.isSliderHorizontal();

	}

	/**
	 * Just do the job.
	 */
	public void apply() {
		if (!reduceWidth()) {
			restoreWidth();
			if (horizontal && !restoreX()) {
				x = view.getViewWidth() - width - MARGIN_X;
			} else if (!horizontal && !restoreY()) {
				y = view.getViewHeight() - MARGIN_Y;
			}
		}
		number.setSliderLocation(x, y, true);
	}


	private boolean reduceWidth() {
		if (horizontal && width > view.getWidth()) {
				number.setSliderWidth(
view.getViewWidth() - 2 * MARGIN_X);
			x = MARGIN_X;
				return true;

			} else if (width > view.getHeight()) {
				number.setSliderWidth(
view.getViewHeight() - 2 * MARGIN_Y);
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
		if (x != origX) {
			x = Math.min(origX,
 view.getViewWidth() - width - MARGIN_X);
			return true;
		}
		return false;
	}

	private boolean restoreY() {
		if (y != origY) {
			y = Math.min(origY, view.getViewHeight() - MARGIN_X);
			return true;
		}
		return false;
	}
}
