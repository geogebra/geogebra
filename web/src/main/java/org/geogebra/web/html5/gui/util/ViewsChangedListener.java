package org.geogebra.web.html5.gui.util;

/**
 * Listener for opening or closing views (e.g. in order to update the entries in
 * the open-view menu
 */
public interface ViewsChangedListener {

	/**
	 * method that is called when a view is opened or closed
	 */
	public void onViewsChanged();

}
