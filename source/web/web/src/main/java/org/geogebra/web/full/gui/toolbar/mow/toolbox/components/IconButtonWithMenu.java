package org.geogebra.web.full.gui.toolbar.mow.toolbox.components;

import java.util.List;

import org.geogebra.web.full.gui.toolbar.mow.toolbox.NotesToolbox;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.ToolboxPopupPositioner;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.JsEval;
import org.geogebra.web.html5.main.toolbox.CustomIconSpec;

public class IconButtonWithMenu extends IconButton {
	private final AppW appW;
	private final List<Integer> tools;
	private CategoryMenuPopup iconButtonPopup;

	/**
	 * Constructor
	 * @param appW - application
	 * @param icon - image
	 * @param ariaLabel - aria label
	 * @param tools - list of tools showing in the popup
	 * @param deselectButtons - deselect button callback
	 * @param toolbox - notes toolbox
	 */
	public IconButtonWithMenu(AppW appW, IconSpec icon, String ariaLabel,
			List<Integer> tools, Runnable deselectButtons, NotesToolbox toolbox) {
		super(appW, icon, ariaLabel, ariaLabel, "", () -> {}, null);
		this.appW = appW;
		this.tools = tools;

		AriaHelper.setAriaHasPopup(this);
		addFastClickHandler((event) -> {
			toolbox.setLastSelectedButtonWithMenu(isActive() ? this : null);
			deselectButtons.run();
			initPopupAndShow(toolbox);
		});
	}

	private void initPopupAndShow(NotesToolbox toolbox) {
		if (iconButtonPopup == null) {
			createPopup();
			addCloseHandler(toolbox);
		}

		showHideMenu();
		updateSelection();
	}

	private void createPopup() {
		iconButtonPopup = new CategoryMenuPopup(appW, tools);
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

	private void addCloseHandler(NotesToolbox toolbox) {
		iconButtonPopup.getPopupPanel().addCloseHandler(e -> {
			deactivate();
			toolbox.removeSelectedButtonWithMenu(this);
			toolbox.onModeChange(appW.getMode());
			AriaHelper.setAriaExpanded(this, false);
		});
	}

	@Override
	public boolean containsMode(int mode) {
		return tools.contains(mode);
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

	/**
	 * Add a custom tool with given properties
	 *
	 * @param url the URL of the tool icon.
	 * @param name The name of the tool.
	 * @param callback the action of the tool.
	 */
	public void addCustomTool(String url, String name, Object callback) {
		if (iconButtonPopup == null) {
			createPopup();
		}
		CustomIconSpec customIconSpec = new CustomIconSpec(url);
		iconButtonPopup.addItem(new AriaMenuItem(name, () -> {
			JsEval.callNativeFunction(callback);
			appW.setMoveMode();
		}, customIconSpec));
	}
}