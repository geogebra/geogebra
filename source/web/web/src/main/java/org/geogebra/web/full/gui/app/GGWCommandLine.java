package org.geogebra.web.full.gui.app;

import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.inputbar.AlgebraInputW;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Wraps the input bar
 *
 */
public class GGWCommandLine extends Composite implements RequiresResize {
	
	private AlgebraInputW algebraInput;

	/**
	 * Create new input bar wrapper
	 */
	public GGWCommandLine() {
		algebraInput = new AlgebraInputW();
		initWidget(algebraInput);
	}

	/**
	 * @param app
	 *            application
	 */
	public void attachApp(App app) {
		algebraInput.init((AppW) app);
	}

	@Override
	public void onResize() {
		algebraInput.onResize();
    }

	/**
	 * @return whether input bar has focus
	 */
	public boolean hasFocus() {
		return algebraInput.hasFocus();
    }
}
