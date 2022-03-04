package org.geogebra.web.full.gui.toolbar;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.Construction;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.gui.util.HasResource;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * @author csilla
 *
 */
public class ToolButton extends StandardButton {

	private final int mode;
	private final AppW appW;
	private final String selectedColor;

	/**
	 * @param mode
	 *            tool mode
	 * @param app
	 *            see {@link AppW}
	 */
	public ToolButton(int mode, AppW app) {
		super(AppResources.INSTANCE.empty(), app.getToolName(mode), 24);
		this.mode = mode;
		this.appW = app;
		this.selectedColor = app.getGeoGebraElement().getPrimaryColor(app.getFrameElement());
		setStyleName("toolButton");
		setAccessible();
		setSelected(false); // update icon
	}

	private void setAccessible() {
		setLabel();
		getElement().setAttribute("mode", mode + "");
		getElement().setId("mode" + mode);
	}

	/**
	 * @param selected
	 *            true if tool is selected -> use teal img
	 */
	public void setSelected(final boolean selected) {
		GGWToolBar.getImageResource(mode, appW, selected ? getFillAdapter() : this);
	}

	private HasResource getFillAdapter() {
		return resource -> {
			if (resource instanceof SVGResource) {
				SVGResource filled = ((SVGResource) resource).withFill(selectedColor);
				this.setResource(filled);
			}
		};
	}

	/**
	 * set localized label of buttons
	 */
	public void setLabel() {
		setLabel(appW.getToolName(mode));
		setAltText(appW.getToolName(mode) + ". " + appW.getToolHelp(mode));
	}

	/**
	 * @return associated mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * @param appMode current mode of the app
	 */
	public void updateSelected(int appMode) {
		boolean selected = (mode == appMode) || isAdditionalToolSelected();
		getElement().setAttribute("selected",
				String.valueOf(selected));
		setSelected(selected);
	}

	private boolean isAdditionalToolSelected() {
		Construction cons = appW.getKernel().getConstruction();
		return (mode == EuclidianConstants.MODE_RULER && cons.getRuler() != null)
				|| (mode == EuclidianConstants.MODE_PROTRACTOR && cons.getProtractor() != null);
	}
}
