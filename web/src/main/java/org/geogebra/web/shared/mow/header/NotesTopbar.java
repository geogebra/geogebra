package org.geogebra.web.shared.mow.header;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class NotesTopbar extends FlowPanel implements SetLabels {
	private final Localization loc;
	private final AppletParameters appletParams;
	private TopbarController controller;
	private final List<IconButton> buttons = new ArrayList<>();
	private IconButton undoBtn;
	private IconButton redoBtn;

	/**
	 * constructor
	 * @param appW - application
	 */
	public NotesTopbar(AppW appW) {
		this.loc = appW.getLocalization();
		this.appletParams = appW.getAppletParameters();
		controller = new TopbarController(appW);
		addStyleName("topbar");
		buildGui();
	}

	private void buildGui() {
		addMenuButton();
		addUndoRedo();
	}

	private void addMenuButton() {
		if (!GlobalHeader.isInDOM() && appletParams.getDataParamShowMenuBar(false)) {
			addSmallPressButton(MaterialDesignResources.INSTANCE.toolbar_menu_black(), "Menu",
					() -> controller.onMenuToggle());
			addDivider();
		}
	}

	private void addUndoRedo() {
		if (appletParams.getDataParamEnableUndoRedo()) {
			undoBtn = addSmallPressButton(MaterialDesignResources.INSTANCE.undo_border(), "Undo",
					() -> controller.onUndo());
			redoBtn = addSmallPressButton(MaterialDesignResources.INSTANCE.redo_border(), "Redo",
					() -> controller.onRedo());
			addDivider();
		}
	}

	/**
	 * update style of undo+redo buttons
	 * @param kernel - kernel
	 */
	public void updateUndoRedoActions(Kernel kernel) {
		kernel.getConstruction().getUndoManager().setAllowCheckpoints(
				appletParams.getParamAllowUndoCheckpoints());
		undoBtn.setDisabled(!kernel.undoPossible());
		redoBtn.setDisabled(!kernel.redoPossible());
	}

	private IconButton addSmallPressButton(SVGResource image, String ariaLabel,
			Runnable clickHandler) {
		IconButton button = new IconButton(loc, clickHandler, image, ariaLabel);
		add(button);
		buttons.add(button);

		return button;
	}

	private void addDivider() {
		SimplePanel divider = new SimplePanel();
		divider.addStyleName("divider");
		add(divider);
	}

	@Override
	public void setLabels() {
		buttons.forEach(SetLabels::setLabels);
	}
}
