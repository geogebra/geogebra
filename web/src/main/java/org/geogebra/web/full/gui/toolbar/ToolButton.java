package org.geogebra.web.full.gui.toolbar;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.toolbar.mow.SubMenuPanel;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

/**
 * @author csilla
 *
 */
public class ToolButton extends StandardButton {
	private int mode;
	private AppW appW;

	/**
	 * @param mode
	 *            tool mode
	 * @param app
	 *            see {@link AppW}
	 * @param panel
	 *            which contains the button
	 */
	public ToolButton(int mode, AppW app, SubMenuPanel panel) {
		super(GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE, mode,
				app),
				app.getLocalization().getMenu(
						EuclidianConstants.getModeText(mode)),
				24, app);
		this.mode = mode;
		this.appW = app;
		addStyleName("toolButton");
		setAccessible();
		this.addFastClickHandler(panel);
	}
	
	private void setAccessible() {
		String altText = appW.getLocalization()
				.getMenu(EuclidianConstants.getModeText(mode)) + ". "
				+ appW.getToolHelp(mode);
		setAltText(altText);
		getElement().setAttribute("mode", mode + "");
		getElement().setId("mode" + mode);
	}

	/**
	 * @param selected
	 *            true if tool is selected -> use teal img
	 */
	public void setSelected(boolean selected) {
		this.setIcon(selected
				? GGWToolBar.getColoredImageForMode(
						ToolbarSvgResources.INSTANCE, mode, appW)
				: GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE,
						mode, appW));
	}
}
