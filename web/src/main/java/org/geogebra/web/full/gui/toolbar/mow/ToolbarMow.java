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
	private FlowPanel toolbarPanel;

	/**
	 * Tab ids.
	 */
	enum TabIds {
		/** tab one */
		PEN,

		/** tab two */
		TOOLS,

		/** tab three */
		MEDIA
	}

	/**
	 * constructor
	 * 
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolbarMow(AppW app) {
		this.appW = app;
		header = new HeaderMow(this, appW);
		add(header);
		initGui();
	}

	private void initGui() {
		addStyleName("toolbarMow");
		toolbarPanel = new FlowPanel();
		toolbarPanel.addStyleName("toolbarMowPanel");
		add(toolbarPanel);
	}

	/**
	 * @param tab
	 *            id of tab
	 */
	public void tabSwitch(TabIds tab) {
		// TODO switch tab and toolbar panel
	}

	public void onModeChange(int mode) {
		// TODO
	}
}
