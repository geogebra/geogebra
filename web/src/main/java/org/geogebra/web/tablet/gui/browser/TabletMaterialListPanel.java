package org.geogebra.web.tablet.gui.browser;

import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.gui.browser.MaterialListPanel;
import org.geogebra.web.full.gui.laf.GLookAndFeel;
import org.geogebra.web.html5.main.AppW;

/**
 * Material list panel for tablets
 */
public class TabletMaterialListPanel extends MaterialListPanel {

	/**
	 * @param app
	 *            application
	 */
	public TabletMaterialListPanel(final AppW app) {
		super(app);
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.setPixelSize(NavigatorUtil.getWindowWidth(),
					NavigatorUtil.getWindowHeight());
		} else {
			this.setPixelSize(NavigatorUtil.getWindowWidth(), NavigatorUtil.getWindowHeight()
					- GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
	}

	@Override
	public void onResize(int appWidth, int appHeight) {
		if (app.getConfig().isSimpleMaterialPicker()) {
			this.setPixelSize(appWidth, appHeight);
		} else {
			this.setPixelSize(appWidth,
					appHeight - GLookAndFeel.BROWSE_HEADER_HEIGHT);
		}
	}

}
