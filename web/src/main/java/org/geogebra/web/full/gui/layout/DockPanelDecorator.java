package org.geogebra.web.full.gui.layout;

import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Adds UI elements to view panels
 */
public interface DockPanelDecorator {

	/**
	 * Wraps the view panel in another panel together with additional UI (e.g.
	 * algebra header)
	 * 
	 * @param algebrap
	 *            algebra scroll panel
	 * @param app
	 *            application
	 * @return wrapped panel
	 */
	Panel decorate(ScrollPanel algebrap, AppW app);

	void addLogo(FlowPanel wrapper, AppW app);

	void onResize(AlgebraViewW aview, int offsetHeight);

	void setLabels();
}
