package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.MyModeChangedListener;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Toolbar for mow
 * 
 * @author csilla
 *
 */
public class ToolbarMow extends FlowPanel implements MyModeChangedListener {
	private AppW appW;
	private HeaderMow header;

	/**
	 * constructor
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolbarMow(AppW app) {
		this.appW = app;
		header = new HeaderMow(this, app);
		add(header);
		addStyleName("toolbarMow");
	}

	public void onModeChange(int mode) {
		// TODO
	}

}
