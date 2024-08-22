package org.geogebra.web.shared.mow.header;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TRANSLATEVIEW;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.CoordSystemListener;
import org.geogebra.common.euclidian.ModeChangeListener;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.gwtutil.NavigatorUtil;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.css.ZoomPanelResources;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.gui.zoompanel.ZoomPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.resources.SVGResource;
import org.geogebra.web.shared.GlobalHeader;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.SimplePanel;

import elemental2.dom.DomGlobal;

public class NotesTopBar extends FlowPanel implements SetLabels, CoordSystemListener,
		ModeChangeListener {
	private final AppletParameters appletParams;
	private final TopbarController controller;
	private final List<IconButton> buttons = new ArrayList<>();
	private IconButton undoBtn;
	private IconButton redoBtn;
	private IconButton homeBtn;
	private IconButton dragBtn;
	private IconButton fullscreenButton;

	/**
	 * constructor
	 * @param appW - application
	 */
	public NotesTopBar(AppW appW) {
		this.appletParams = appW.getAppletParameters();
		controller = new TopbarController(appW);
		if (appW.getActiveEuclidianView() != null) {
			appW.getActiveEuclidianView().getEuclidianController().addZoomerListener(this);
		}
		addStyleName("topbar");
		buildGui();
	}

	/**
	 * @return whether the topbar is attached
	 */
	public boolean wasAttached() {
		return getElement().hasChildNodes();
	}

	private void buildGui() {
		addMenuButton();
		addUndoRedo();
		addZoomButtons();
		addFullscreenButton();
		addSettingsButton();
	}

	private void addMenuButton() {
		if (!GlobalHeader.isInDOM() && appletParams.getDataParamShowMenuBar(false)) {
			addSmallPressButton(MaterialDesignResources.INSTANCE.toolbar_menu_black(), "Menu",
					() -> controller.onMenuToggle(), AccessibilityGroup.MENU);
			addDivider();
		}
	}

	private void addUndoRedo() {
		if (appletParams.getDataParamEnableUndoRedo()) {
			undoBtn = addSmallPressButton(MaterialDesignResources.INSTANCE.undo_border(), "Undo",
					() -> controller.onUndo(), AccessibilityGroup.UNDO);
			redoBtn = addSmallPressButton(MaterialDesignResources.INSTANCE.redo_border(), "Redo",
					() -> controller.onRedo(), AccessibilityGroup.REDO);
			addDivider();
		}
	}

	private void addZoomButtons() {
		if (!ZoomPanel.needsZoomButtons(controller.getApp())) {
			return;
		}

		if (!NavigatorUtil.isMobile()) {
			addSmallPressButton(GuiResourcesSimple.INSTANCE.zoom_in(), "ZoomIn.Tool",
					() -> controller.onZoomIn(), AccessibilityGroup.ZOOM_NOTES_PLUS);
			addSmallPressButton(GuiResourcesSimple.INSTANCE.zoom_out(), "ZoomOut.Tool",
					() -> controller.onZoomOut(), AccessibilityGroup.ZOOM_NOTES_MINUS);
		}

		homeBtn = addSmallPressButton(ZoomPanelResources.INSTANCE.home_zoom_black18(),
				"StandardView", () -> controller.onHome(), AccessibilityGroup.ZOOM_NOTES_HOME);
		homeBtn.setDisabled(true);

		addDragButton();
		addDivider();
	}

	private void addDragButton() {
		dragBtn = new IconButton(controller.getApp(), MaterialDesignResources
				.INSTANCE.move_canvas(), "PanView", "PanView", "", null);
		dragBtn.addFastClickHandler((event) -> {
			controller.onDrag(dragBtn.isActive());
		});

		registerFocusable(dragBtn, AccessibilityGroup.ZOOM_NOTES_DRAG_VIEW);
		styleAndRegisterTopbarButton(dragBtn);
	}

	private void styleAndRegisterTopbarButton(IconButton button) {
		button.addStyleName("small");
		buttons.add(button);
		add(button);
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

	private void addFullscreenButton() {
		if (controller.needsFullscreenButton()) {
			fullscreenButton = addSmallPressButton(ZoomPanelResources.INSTANCE
					.fullscreen_black18(), "Fullscreen", null,
					AccessibilityGroup.FULL_SCREEN_NOTES);
			fullscreenButton.addFastClickHandler(source ->
					controller.onFullscreenOn(fullscreenButton));

			controller.getApp().getGlobalHandlers().addEventListener(DomGlobal.document,
					Browser.getFullscreenEventName(), event -> {
				if (!Browser.isFullscreen()) {
					controller.onFullscreenExit(fullscreenButton);
				}
			});

			addDivider();
		}
	}

	private void addSettingsButton() {
		if (controller.getApp().allowStylebar()) {
			IconButton settingsBtn = addSmallPressButton(MaterialDesignResources.INSTANCE.gear(),
					"Settings", null, null);
			FocusableWidget focusableSettingsBtn = controller.getRegisteredFocusable(
					AccessibilityGroup.SETTINGS_NOTES, settingsBtn);

			settingsBtn.addFastClickHandler(source -> controller.onSettingsOpen(settingsBtn,
					focusableSettingsBtn));
		}
	}

	private IconButton addSmallPressButton(SVGResource image, String ariaLabel,
			Runnable clickHandler, AccessibilityGroup group) {
		IconButton button = new IconButton(controller.getApp(), clickHandler, image, ariaLabel);
		add(button);
		buttons.add(button);

		if (group != null) {
			controller.registerFocusable(button, group);
		}

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

	private void registerFocusable(IconButton button, AccessibilityGroup group) {
		controller.registerFocusable(button, group);
	}

	@Override
	public void onModeChange(int mode) {
		if (dragBtn == null) {
			return;
		}

		dragBtn.setActive(mode == MODE_TRANSLATEVIEW);
	}
}
