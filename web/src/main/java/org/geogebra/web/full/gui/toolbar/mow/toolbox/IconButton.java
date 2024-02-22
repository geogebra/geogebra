package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;

public class IconButton extends StandardButton {
	private SVGResource image;

	/**
	 * Constructor
	 * @param mode - tool mode
	 * @param appW - application
	 */
	public IconButton(int mode, AppW appW) {
		super(GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE, mode, appW),
				appW.getToolName(mode), 24);
		addStyleName("iconButton");
	}

	/**
	 * Constructor press icon button
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - on press handler
	 */
	public IconButton(Localization loc, SVGResource icon, String ariaLabel, Runnable onHandler) {
		super(icon, 24);
		addStyleName("iconButton");
		image = icon;
		AriaHelper.setLabel(this, loc.getMenu(ariaLabel));
		addFastClickHandler(event -> {
			if (!isDisabled() && onHandler != null) {
				onHandler.run();
			}
		});
	}

	/**
	 * Constructor toggle icon button
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(Localization loc, SVGResource icon, String ariaLabel, Runnable onHandler,
			Runnable offHandler) {
		super(icon, 24);
		addStyleName("iconButton");
		image = icon;
		AriaHelper.setLabel(this, loc.getMenu(ariaLabel));
		addFastClickHandler(event -> {
			if (!isDisabled()) {
				if (isActive() && offHandler != null) {
					offHandler.run();
				} else {
					onHandler.run();
				}
				setActive(!isActive());
			}
		});
	}

	/**
	 * Constructor toggle icon button with dataTest
	 * @param loc - localization
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTest - id for ui test
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(Localization loc, SVGResource icon, String ariaLabel, String dataTest,
			Runnable onHandler, Runnable offHandler) {
		this(loc, icon, ariaLabel, onHandler, offHandler);
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

	private void setActive(boolean isActive) {
		AriaHelper.setPressedState(this, isActive);
		Dom.toggleClass(this, "active", isActive);
		setIcon(image.withFill(isActive ? GColor.PURPLE_A700.toString() : GColor.BLACK.toString()));
	}

	private boolean isActive() {
		return getElement().hasClassName("active");
	}

	private boolean isDisabled() {
		return getElement().hasClassName("disabled");
	}
}
