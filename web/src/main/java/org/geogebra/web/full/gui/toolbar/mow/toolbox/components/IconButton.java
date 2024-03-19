package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;

public class IconButton extends StandardButton implements SetLabels {
	private static final int DEFAULT_BUTTON_WIDTH = 24;
	private SVGResource image;
	private String ariaLabelTransKey;
	private String dataTitleTransKey;
	private final Localization localization;

	/**
	 * Constructor
	 * @param mode - tool mode
	 * @param appW - application
	 */
	public IconButton(int mode, AppW appW) {
		super(GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE, mode, appW),
				appW.getToolName(mode), DEFAULT_BUTTON_WIDTH);
		addStyleName("iconButton");
		localization = appW.getLocalization();
	}

	/**
	 * Constructor press icon button
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - on press handler
	 */
	public IconButton(Localization loc, SVGResource icon, String ariaLabel, Runnable onHandler) {
		this(loc, icon, ariaLabel);
		addFastClickHandler(event -> {
			if (!isDisabled() && onHandler != null) {
				onHandler.run();
			}
		});
	}

	private IconButton(Localization loc, SVGResource icon, String ariaLabel) {
		super(icon, DEFAULT_BUTTON_WIDTH);
		addStyleName("iconButton");
		image = icon;
		AriaHelper.setLabel(this, loc.getMenu(ariaLabel));
		ariaLabelTransKey = ariaLabel;
		localization = loc;
	}

	/**
	 * Constructor toggle icon button
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - tooltip
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			Runnable onHandler, Runnable offHandler) {
		this(appW.getLocalization(), icon, ariaLabel);
		dataTitleTransKey = dataTitle;
		AriaHelper.setTitle(this, appW.getLocalization().getMenu(dataTitle));
		addFastClickHandler(event -> {
			if (!isDisabled()) {
				if (isActive() && offHandler != null) {
					offHandler.run();
				} else {
					onHandler.run();
				}
				setActive(!isActive(),
						appW.getGeoGebraElement().getDarkColor(appW.getFrameElement()));
			}
		});
	}

	/**
	 * Constructor toggle icon button with dataTest
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - tooltip
	 * @param dataTest - id for ui test
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(AppW appW, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler, Runnable offHandler) {
		this(appW, icon, ariaLabel, dataTitle, onHandler, offHandler);
		TestHarness.setAttr(this, dataTest);
	}

	/**
	 * Constructor
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - test
	 * @param onHandler - on press handler
	 */
	public IconButton(Localization loc, SVGResource icon, String ariaLabel, String dataTitle,
			String dataTest, Runnable onHandler) {
		this(loc, icon, ariaLabel, onHandler);
		dataTitleTransKey = dataTitle;
		AriaHelper.setTitle(this, loc.getMenu(dataTitle));
		TestHarness.setAttr(this, dataTest);
	}

	/**
	 * Disable button
	 * @param isDisabled - whether is disabled or not
	 */
	public void setDisabled(boolean isDisabled) {
		AriaHelper.setAriaDisabled(this, isDisabled);
		Dom.toggleClass(this, "disabled", isDisabled);
	}

	/**
	 * @param isActive - whether is on or off
	 */
	public void setActive(boolean isActive, String selectionColor) {
		AriaHelper.setPressedState(this, isActive);
		Dom.toggleClass(this, "active", isActive);
		setIcon(image.withFill(isActive ? selectionColor : GColor.BLACK.toString()));
	}

	public boolean isActive() {
		return getElement().hasClassName("active");
	}

	private boolean isDisabled() {
		return getElement().hasClassName("disabled");
	}

	/**
	 * Updates the image and the aria attributes
	 * @param image - image
	 * @param mode - tool mode
	 * @param appW - application
	 */
	public void updateImgAndTxt(SVGResource image, int mode, AppW appW) {
		this.image = image;
		setIcon(image);
		String toolName = appW.getToolName(mode);
		setAltText(toolName + ". " + appW.getToolHelp(mode));
		AriaHelper.setDataTitle(this, toolName);
		TestHarness.setAttr(this, "selectModeButton" + mode);
	}

	/**
	 * Updates the aria label and the data title for this Icon Button (e.g. when language changes)
	 */
	@Override
	public void setLabels() {
		AriaHelper.setLabel(this, localization.getMenu(ariaLabelTransKey));
		AriaHelper.setDataTitle(this, localization.getMenu(dataTitleTransKey));
	}
}
