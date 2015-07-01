package org.geogebra.web.phone.gui.container;

import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.phone.gui.view.AbstractView;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Interface defining the methods to operate on widgets that contain views.
 */
public interface ViewContainer  extends ResizeListener, IsWidget {

	/**
	 * Adds a view to the container. If the container was empty before,
	 * it will show the newly added view.
	 * @param view the view to be added
	 */
	void addView(AbstractView view);

	/**
	 * Removes a view from the container. If the view was shown previously,
	 * the container will show the last view of the container.
	 * @param view the view to be removed.
	 */
	void removeView(AbstractView view);

	/**
	 * Shows the view. Does nothing if the view is no part of the container,
	 * or if it is already showing.
	 * @param view the view to be shown
	 */
	void showView(AbstractView view);

}
