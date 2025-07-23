package org.geogebra.web.full.gui.layout.scientific;

import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.toolbox.components.IconButton;
import org.geogebra.web.full.gui.toolbarpanel.UndoRedoProvider;
import org.geogebra.web.html5.gui.view.ImageIconSpec;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.user.client.ui.FlowPanel;

public class ScientificEmbedTopBar extends FlowPanel {
	private final static int LEFT_PADDING_WITH_NAV_RAIL = 88;
	private final static int LEFT_PADDING = 16;
	/**
	 * Padding if floating menu is shown on top of calc.
	 * It would be better to have the menu button in the top bar to fix centering,
	 * but that's out of scope for APPS-6498 */
	private static final int LEFT_PADDING_WITH_MENU = 56;
	public final AppW appW;
	private final UndoRedoProvider undoRedoProvider;

	/**
	 * Top bar including undo/redo and settings based on applet parameters.
	 * Used only for embedded scientific calculator.
	 * @param appW application
	 */
	public ScientificEmbedTopBar(AppW appW) {
		this.appW = appW;
		undoRedoProvider = new UndoRedoProvider(appW);
		addStyleName("scientificEmbedTopBar");

		buildTopBar();
		updateTopBarVisibility();
	}

	private void buildTopBar() {
		if (isUndoRedoEnabled()) {
			add(undoRedoProvider.getUndoButton());
			add(undoRedoProvider.getRedoButton());
		}
		if (isSettingsEnabled()) {
			addSettingsButton();
		}
	}

	private void addSettingsButton() {
		IconButton settingsButton = new IconButton(appW, () -> appW.getDialogManager()
				.showPropertiesDialog(OptionType.GLOBAL, null), new ImageIconSpec(
				MaterialDesignResources.INSTANCE.gear()), "Settings");
		settingsButton.addStyleName("settingsBtnScientific");
		add(settingsButton);
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
		undoRedoProvider.updateUndoRedoActions();
	}

	/**
	 * Update undo button distance from the left side, as it changes with
	 * portrait/landscape view.
	 */
	public void updateUndoRedoPosition() {
		getElement().getStyle().setPaddingLeft(appW.isPortrait() ? getPortraitPadding()
				: LEFT_PADDING_WITH_NAV_RAIL, Unit.PX);
	}

	private double getPortraitPadding() {
		return appW.showMenuBar() ? LEFT_PADDING_WITH_MENU : LEFT_PADDING;
	}
}
