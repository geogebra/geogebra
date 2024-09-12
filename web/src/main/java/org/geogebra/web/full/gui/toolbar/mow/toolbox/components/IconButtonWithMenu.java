package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

public class IconButtonWithMenu extends IconButton {
	private final AppW appW;
	private final List<Integer> tools;
	private CategoryMenuPopup iconButtonPopup;
	private final NotesToolbox toolbox;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - aria label
	 * @param tools - list of tools showing in the popup
	 * @param deselectButtons - deselect button callback
	 * @param toolbox - notes toolbox
	 */
	public IconButtonWithMenu(AppW appW, SVGResource icon, String ariaLabel,
			List<Integer> tools, Runnable deselectButtons, NotesToolbox toolbox) {
		super(appW, icon, ariaLabel, ariaLabel, "", () -> {}, null);
		this.appW = appW;
		this.tools = tools;
		this.toolbox = toolbox;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			deselectButtons.run();
			initPopupAndShow();
		});
	}

	private void initPopupAndShow() {
		if (iconButtonPopup == null) {
			iconButtonPopup = new CategoryMenuPopup(appW, tools);
			addCloseHandler();
		}

		showHideMenu();
		updateSelection();
	}

	private void updateSelection() {
		AriaHelper.setAriaExpanded(this, getPopup().isShowing());
		setActive(getPopup().isShowing());
	}

	private void showHideMenu() {
		if (getPopup().isShowing()) {
			iconButtonPopup.hide();
		} else {
			ToolboxPopupPositioner.showRelativeToToolbox(getPopup(), this, appW);
		}
	}

	private void addCloseHandler() {
		iconButtonPopup.getPopupPanel().addCloseHandler(e -> {
			deactivate();
			toolbox.onModeChange(appW.getMode());
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	private GPopupPanel getPopup() {
		return iconButtonPopup.getPopupPanel();
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (iconButtonPopup != null) {
			iconButtonPopup.setLabels();
		}
	}
}