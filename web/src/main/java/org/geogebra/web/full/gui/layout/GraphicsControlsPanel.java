package org.geogebra.web.full.gui.layout;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.main.App;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.ContextMenuGraphicsWindowW;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.user.client.ui.FlowPanel;

public class GraphicsControlsPanel extends FlowPanel implements DockControlPanel {
	private static final int GEAR_CONTEXT_MENU_MARGIN = 16;
	private final AppW app;
	StandardButton graphicsContextMenuBtn;

	/**
	 * Panel wrapping the settings icon and optional controls
	 * @param app application
	 * @param parent parent dock panel
	 */
	public GraphicsControlsPanel(AppW app, DockPanelW parent) {
		this.app = app;
		if (app.allowStylebar()) {
			addSettingsIcon(parent);
		}
		setStyleName("graphicsControlsPanel");
	}

	private void addSettingsIcon(final DockPanelW parent) {
		graphicsContextMenuBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.settings_border(), null, 24);
		graphicsContextMenuBtn
				.setTitle(app.getLocalization().getMenu("Settings"));
		final FocusableWidget focusableWidget = new FocusableWidget(
				AccessibilityGroup.getViewGroup(parent.getViewId()),
				AccessibilityGroup.ViewControlId.SETTINGS_BUTTON,  graphicsContextMenuBtn);
		if (parent.getViewId() == App.VIEW_EUCLIDIAN) {
			focusableWidget.attachTo(app);
		}

		graphicsContextMenuBtn.addFastClickHandler(source -> {
			app.getAccessibilityManager().setAnchor(focusableWidget);
			onGraphicsSettingsPressed(parent);
		});

		graphicsContextMenuBtn.addStyleName("flatButton");
		graphicsContextMenuBtn.addStyleName(app.isWhiteboardActive()
				? "graphicsContextMenuBtn mow" : "graphicsContextMenuBtn");
		graphicsContextMenuBtn.getElement().setTabIndex(0);
		TestHarness.setAttr(graphicsContextMenuBtn, "graphicsViewContextMenu");
		add(graphicsContextMenuBtn);
	}

	/** Graphics Settings button handler */
	private void onGraphicsSettingsPressed(DockPanelW parent) {
		app.closeMenuHideKeyboard();
		if (app.isWhiteboardActive() && app.getAppletFrame() != null
				&& app.getAppletFrame() instanceof GeoGebraFrameFull) {
			((GeoGebraFrameFull) app.getAppletFrame()).deselectDragBtn();
		}

		final ContextMenuGraphicsWindowW contextMenu = parent.getGraphicsWindowContextMenu();

		final GPopupPanel popup = contextMenu.getWrappedPopup().getPopupPanel();
		popup.setPopupPositionAndShow((offsetWidth, offsetHeight) -> {
			popup.setPopupPosition((int) app.getWidth() - offsetWidth - GEAR_CONTEXT_MENU_MARGIN,
					GEAR_CONTEXT_MENU_MARGIN);
			contextMenu.getWrappedPopup().getPopupMenu().focusDeferred();
		});

		popup.addCloseHandler(event -> {
			if (event.isAutoClosed()) {
				app.getEuclidianView1().getEuclidianController()
						.setPopupJustClosed(true);
			}
		});
	}

	@Override
	public void setLabels() {
		if (graphicsContextMenuBtn != null) {
			String titletext = app.getLocalization().getMenu("Settings");
			graphicsContextMenuBtn.setTitle(titletext);
			graphicsContextMenuBtn.setAltText(titletext);
		}
	}

	@Override
	public void setLayout() {
		// no layout
	}
}
