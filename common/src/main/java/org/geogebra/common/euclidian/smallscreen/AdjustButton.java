package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.util.debug.Log;

public class AdjustButton extends AdjustWidget {
	private static final int MARGIN_X = 5;
	static final int MARGIN_Y = 5;
	private GeoButton button;
	private int origX;
	private int origY;
	// private int origWidth;
	// private int origHeight;

	public AdjustButton(GeoButton button, EuclidianView view) {
		super(view);
		this.button = button;
		x = button.getAbsoluteScreenLocX();
		y = button.getAbsoluteScreenLocY();

		origX = button.getOrigX() == null ? 0 : button.getOrigX();
		origY = button.getOrigY() == null ? 0 : button.getOrigY();
		// origWidth = button.getOrigWidth() == null ? 0 :
		// button.getOrigWidth();
		// origHeight = button.getOrigHeight() == null ? 0
		// : button.getOrigHeight();

		width = button.getWidth();
		height = button.getHeight();
		Log.debug(button.getLabelSimple() + " w: " + width + " h: " + height);
	}

	@Override
	public boolean isOnScreen() {
		return isXOnScreen() && isYOnScreen();
	}

	/**
	 * @return if button is on screen horizontally.
	 */
	public boolean isXOnScreen() {
		return Kernel.isEqual(x, origX) && x + width < view.getViewWidth();
	}

	/**
	 * @return if button is on screen vertically.
	 */
	public boolean isYOnScreen() {
		return Kernel.isEqual(y, origY) && y + height < view.getViewHeight();
	}


	@Override
	public void apply() {
		if (isOnScreen()) {
			Log.debug(
					"[AS] Button " + button.getLabelSimple() + " is on screen");
			return;
		}

		Log.debug("[AS] Button " + button.getLabelSimple()
				+ " is NOT  on screen");

		int viewWidth = view.getViewWidth();
		int viewHeight = view.getViewHeight();

		if (width > viewWidth - MARGIN_X) {
			width = viewWidth - MARGIN_X;
			button.setWidth((int) width);
		}

		if (height > viewHeight - MARGIN_Y) {
			height = viewHeight - MARGIN_Y;
			button.setHeight((int) height);
		}

		boolean changed = false;
		if (x + width > viewWidth - MARGIN_X) {
			x = viewWidth - width - MARGIN_X;
			changed = true;
		}

		// if (y + height > viewHeight - MARGIN_Y) {
		// y = viewHeight - height - MARGIN_Y;
		// changed = true;
		// }

		if (changed) {
			button.setAbsoluteScreenLoc((int) x, (int) y);
		}

	}

}
