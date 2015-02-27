package geogebra.tablet.gui.browser;

import geogebra.html5.gui.ResizeListener;
import geogebra.html5.main.AppW;
import geogebra.web.gui.browser.BrowseGUI;
import geogebra.web.gui.laf.GLookAndFeel;

import com.google.gwt.user.client.Window;

public class TabletBrowseGUI extends BrowseGUI {

	public TabletBrowseGUI(final AppW app) {
		super(app, null);
	}

	@Override
	protected void addContent() {
		this.container.add(this.materialListPanel);
		this.setContentWidget(this.container);
	}

	@Override
	protected void initMaterialListPanel() {
		this.materialListPanel = new TabletMaterialListPanel(app);
		this.addResizeListener(this.materialListPanel);
	}

	@Override
	protected void updateViewSizes() {
		this.container.setPixelSize(Window.getClientWidth(),
		        Window.getClientHeight() - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		for (final ResizeListener res : this.resizeListeners) {
			res.onResize();
		}
	}
}
