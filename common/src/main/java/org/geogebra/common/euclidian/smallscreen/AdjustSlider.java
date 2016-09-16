package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Adjusts slider position on file load
 */
public class AdjustSlider extends AdjustWidget {
	private GeoNumeric number;
	private boolean horizontal;

	private static final int MARGIN_X = 15;
	private static final int MARGIN_Y = 15;

	/**
	 * @param num
	 *            slider
	 * @param view
	 *            view
	 */
	public AdjustSlider(GeoNumeric num, EuclidianView view) {
		super(view);

		this.number = num;

		x = number.getSliderX();
		origX = number.getOrigSliderX();

		y = number.getSliderY();
		origY = number.getOrigSliderY();

		width = number.getSliderWidth();
		origWidth = number.getOrigSliderWidth() == null ? 0
				: number.getOrigSliderWidth().doubleValue();

		horizontal = number.isSliderHorizontal();

	}

	@Override
	public boolean isOnScreen() {
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

	@Override
	public void apply() {
		if (isOnScreen()) {
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
