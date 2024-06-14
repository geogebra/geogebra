package org.geogebra.web.shared.mow.header;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.main.Localization;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

public class NotesTopbar extends FlowPanel implements SetLabels, CoordSystemListener {
	private final Localization loc;
	private final AppletParameters appletParams;
	private TopbarController controller;
	private final List<IconButton> buttons = new ArrayList<>();
	private IconButton undoBtn;
	private IconButton redoBtn;
	private IconButton homeBtn;
	private IconButton dragBtn;
	private final Runnable deselectDragBtn = (() -> {
			if (dragBtn != null && controller.getApp().getMode()
					== EuclidianConstants.MODE_TRANSLATEVIEW) {
				dragBtn.setActive(false);
				controller.getApp().setMode(EuclidianConstants.MODE_SELECT_MOW);
			}
		});

	/**
	 * constructor
	 * @param appW - application
	 */
	public NotesTopbar(AppW appW) {
		this.loc = appW.getLocalization();
		this.appletParams = appW.getAppletParameters();
		controller = new TopbarController(appW, deselectDragBtn);
		if (appW.getActiveEuclidianView() != null) {
			appW.getActiveEuclidianView().getEuclidianController().addZoomerListener(this);
		}
		addStyleName("topbar");
		buildGui();
	}

	private void buildGui() {
		addMenuButton();
		addUndoRedo();
		addZoomButtons();
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

	private void addZoomButtons() {
		if (!ZoomPanel.needsZoomButtons(controller.getApp())) {
			return;
		}

		if (!NavigatorUtil.isMobile()) {
			addSmallPressButton(GuiResourcesSimple.INSTANCE.zoom_in(), "ZoomIn.Tool",
					() -> controller.onZoomIn());
			addSmallPressButton(GuiResourcesSimple.INSTANCE.zoom_out(), "ZoomOut.Tool",
					() -> controller.onZoomOut());
		}

		homeBtn = addSmallPressButton(ZoomPanelResources.INSTANCE.home_zoom_black18(),
				"StandardView", () -> controller.onHome());
		homeBtn.setDisabled(true);

		addDragButton();
		addDivider();
	}

	private void addDragButton() {
		dragBtn = new IconButton(controller.getApp(), MaterialDesignResources
				.INSTANCE.move_canvas(), "PanView", "PanView", () -> controller.onDrag(), null);
		dragBtn.addStyleName("small");
		buttons.add(dragBtn);
		add(dragBtn);
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

	@Override
	public void onCoordSystemChanged() {
		controller.updateHomeButtonVisibility(homeBtn);
	}

	public void deselectDragButton() {
		deselectDragBtn.run();
	}
}
