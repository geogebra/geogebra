package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButton extends StandardButton {

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
	 * Constructor
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - on press handler
	 */
	public IconButton(ResourcePrototype icon, String ariaLabel, Runnable onHandler) {
		super(icon, 24);
		addStyleName("iconButton");
		AriaHelper.setLabel(this, ariaLabel);
		addFastClickHandler(event -> onHandler.run());
	}

	/**
	 * Constructor
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param onHandler - switch on handler
	 * @param offHandler - switch off handler
	 */
	public IconButton(ResourcePrototype icon, String ariaLabel, Runnable onHandler,
			Runnable offHandler) {
		super(icon, 24);
		addStyleName("iconButton");
		AriaHelper.setLabel(this, ariaLabel);
		addFastClickHandler(event -> {
			if (isActive() && offHandler != null) {
				offHandler.run();
			} else {
				onHandler.run();
			}
			setActive(!isActive());
		});
	}

	/**
	 * Constructor
	 * @param icon - image
	 * @param ariaLabel - label
	 * @param dataTitle - title
	 * @param dataTest - test
	 * @param onHandler - on press handler
	 */
	public IconButton(SVGResource icon, String ariaLabel, String dataTitle, String dataTest,
			Runnable onHandler) {
		this(icon, ariaLabel, onHandler);
		AriaHelper.setTitle(this, dataTitle);
		AriaHelper.setDataTest(this, dataTest);
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
	}

	private boolean isActive() {
		return getElement().hasClassName("active");
	}
}
