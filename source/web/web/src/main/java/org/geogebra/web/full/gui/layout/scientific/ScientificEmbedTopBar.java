package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

public class ScientificEmbedTopBar extends FlowPanel {
	private final static int LEFT_PADDING_WITH_NAV_RAIL = 88;
	private final static int LEFT_PADDING = 16;
	public final AppW appW;
	private StandardButton undoBtn;
	private StandardButton redoBtn;

	/**
	 * Top bar including undo/redo and settings based on applet parameters.
	 * Used only for embedded scientific calculator.
	 * @param appW application
	 */
	public ScientificEmbedTopBar(AppW appW) {
		this.appW = appW;
		addStyleName("scientificEmbedTopBar");

		buildTopBar();
		updateTopBarVisibility();
	}

	private void buildTopBar() {
		if (isUndoRedoEnabled()) {
			addUndoButton();
			addRedoButton();
		}
		if (isSettingsEnabled()) {
			addSettingsButton();
		}
	}

	private void addUndoButton() {
		undoBtn = createTopBarButton(MaterialDesignResources.INSTANCE.undo_border(),
				"Undo", "undo");
		undoBtn.addFastClickHandler(event -> onUndoPressed());
		add(undoBtn);
	}

	private void addRedoButton() {
		redoBtn = createTopBarButton(MaterialDesignResources.INSTANCE.redo_border(),
				"Redo", "redo");
		redoBtn.addFastClickHandler(event -> onRedoPressed());
		add(redoBtn);
	}

	/**
	 * Handler for Undo button.
	 */
	protected void onUndoPressed() {
		appW.closePopups();
		appW.closeMenuHideKeyboard();
		appW.getGuiManager().undo();
	}

	/**
	 * Handler for Redo button.
	 */
	protected void onRedoPressed() {
		appW.closePopups();
		appW.closeMenuHideKeyboard();
		appW.getGuiManager().redo();
	}

	private void addSettingsButton() {
		StandardButton settings = createTopBarButton(MaterialDesignResources.INSTANCE.gear(),
				"Settings", "settingsBtnScientific");
		settings.addFastClickHandler(source -> appW.getGuiManager().showSciSettingsView());
		add(settings);
	}

	private StandardButton createTopBarButton(SVGResource icon, String title, String className) {
		StandardButton button = new StandardButton(icon, 24);
		button.setTitle(appW.getLocalization().getMenu(title));
		button.addStyleName("flatButtonHeader");
		button.addStyleName(className);

		return button;
	}

	/**
	 * @return iff showMenuBar = true || allowStyleBar = true
	 */
	private boolean isSettingsEnabled() {
		return appW.getAppletParameters().getDataParamAllowStyleBar()
				|| appW.getAppletParameters().getDataParamShowMenuBar(false);
	}

	/**
	 * @return iff enableUndoRedo = true
	 */
	private boolean isUndoRedoEnabled() {
		return appW.getAppletParameters().getDataParamEnableUndoRedo();
	}

	/**
	 * Top bar should be only visible if showMenuBar = true, or
	 * showToolbar = true && (enableUndoRedo = true || allowStyleBar = true)
	 */
	private void updateTopBarVisibility() {
		setVisible(appW.getAppletParameters().getDataParamShowMenuBar(false)
				|| (appW.getAppletParameters().getDataParamShowToolBar(false)
				&& getWidgetCount() != 0));
	}

	/**
	 * Enable/disable undo and redo buttons if undo/redo action is possible.
	 */
	public void updateUndoRedoVisibility() {
		if (undoBtn == null || redoBtn == null) {
			return;
		}

		Dom.toggleClass(undoBtn, "buttonActive", "buttonInactive",
				appW.getKernel().undoPossible());

		if (appW.getKernel().redoPossible()) {
			redoBtn.removeStyleName("hidden");
		} else {
			if (!redoBtn.getElement().hasClassName("hidden")) {
				appW.getAccessibilityManager().focusAnchor();
			}
			redoBtn.addStyleName("hidden");
		}
	}

	/**
	 * Update undo button distance from the left side, as it changes with
	 * portrait/landscape view.
	 */
	public void updateUndoRedoPosition() {
		getElement().getStyle().setPaddingLeft(appW.isPortrait() ? LEFT_PADDING
				: LEFT_PADDING_WITH_NAV_RAIL, Unit.PX);
	}
}
