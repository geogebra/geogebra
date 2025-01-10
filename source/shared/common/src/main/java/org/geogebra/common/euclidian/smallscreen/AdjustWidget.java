package org.geogebra.common.euclidian.smallscreen;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;

/**
 * 
 * @author lac
 *
 */
public abstract class AdjustWidget {
	protected EuclidianView view;
	protected double ratioX;
	protected double ratioY;

	/**
	 * @param view
	 *            view
	 */
	public AdjustWidget(EuclidianView view) {
		this.view = view;
		App app = view.getApplication();
		int fileWidth = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileWidth();
		int fileHeight = app.getSettings()
				.getEuclidian(view.getEuclidianViewNo()).getFileHeight();

		ratioX = fileWidth == 0 ? 1 : (double) view.getViewWidth() / fileWidth;
		ratioY = fileHeight == 0 ? 1
				: (double) view.getViewHeight() / fileHeight;

		// Log.debug("[ADJUST] ratioX: " + ratioX + " ratioY: " + ratioY);

	}

	/**
	 * @return if the entire widget is visible on the screen.
	 */
	public abstract boolean isOnScreen();

	/**
	 * Adjust the widget: if it is offscreen, this method repositions it to be
	 * on screen entirely.
	 */
	public abstract void apply();

}