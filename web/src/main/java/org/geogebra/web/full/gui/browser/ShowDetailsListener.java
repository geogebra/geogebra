package org.geogebra.web.full.gui.browser;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Listener for card expanding
 */
public interface ShowDetailsListener {

	/**
	 * @param content
	 *            card content
	 */
	public abstract void onShowDetails(FlowPanel content);

}
