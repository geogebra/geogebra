package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoButton;
import org.geogebra.common.util.debug.Log;

public class AdjustButton extends AdjustWidget {
	private static final int MARGIN_X = 15;
	private static final int MARGIN_Y = 15;
	private GeoButton button;
	private int viewWidth;
	private int viewHeight;
	public AdjustButton(GeoButton button, EuclidianView view) {
		super(view);
		this.button = button;
		x = button.getAbsoluteScreenLocX();
		y = button.getAbsoluteScreenLocY();
		viewWidth = view.getViewWidth();
		viewHeight = view.getViewHeight();
		width = button.getWidth();
		height = button.getHeight();
		Log.debug(button.getLabelSimple() + " w: " + width + " h: " + height);
	}

	@Override
	public boolean isOnScreen() {
		return x >= 0 && y >= 0 && x + width < viewWidth
				&& y + height < viewHeight;
	}

	@Override
	public void apply() {
		if (isOnScreen()) {
			Log.debug(
					"[AS] Button " + button.getLabelSimple() + " is on screen");
			return;
		}

		Log.debug(
"[AS] Button " + button.getLabelSimple()
				+ " is NOT  on screen");

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

		if (y + height > viewHeight - MARGIN_Y) {
			y = viewHeight - height - MARGIN_Y;
			changed = true;
		}

		if (changed) {
			button.setAbsoluteScreenLoc((int) x,
 (int) y);
		}

	}

}
