package org.geogebra.web.full.gui.layout;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.full.cas.view.CASStylebarW;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.layout.panels.AlgebraStyleBarW;
import org.geogebra.web.full.gui.util.StyleBarW;
import org.geogebra.web.full.gui.view.spreadsheet.SpreadsheetStyleBarW;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.GPushButton;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class TitleBarPanel extends FlowPanel implements DockControlPanel {
	private final AppW app;
	private final DockPanelW dockPanel;
	protected FlowPanel titleBarPanelContent;
	private FlowPanel dragPanel;
	/**
	 * Style bar component.
	 */
	Widget styleBar;
	/**
	 * Panel for the styling bar if one is available.
	 */
	private final FlowPanel styleBarPanel;
	/** button to collapse / expand stylebar */
	protected StandardButton toggleStyleBarButton;
	private final FlowPanel closeButtonPanel;

	protected TitleBarPanel(AppW app, DockPanelW parent) {
		this.app = app;
		this.dockPanel = parent;
		titleBarPanelContent = new FlowPanel();
		styleBarPanel = new FlowPanel();
		styleBarPanel.setStyleName("StyleBarPanel_");
		updateStyles();
		add(titleBarPanelContent);
		NoDragImage closeIcon = new NoDragImage(GuiResourcesSimple.INSTANCE.close(), 24);
		GPushButton closeButton = new GPushButton(closeIcon);
		closeButton.addClickHandler(event ->
				app.getGuiManager().setShowView(false, dockPanel.id));
		closeButtonPanel = new FlowPanel();
		closeButtonPanel.setStyleName("closeButtonPanel");
		closeButtonPanel.setVisible(dockPanel.isStyleBarEmpty());
		closeButtonPanel.add(closeButton);
		if (!dockPanel.isStyleBarEmpty()) {
			initToggleButton();
		}

		titleBarPanelContent.setVisible(!dockPanel.isStyleBarEmpty());

		if (dockPanel.isStyleBarEmpty()) {
			add(closeButtonPanel);
		}
		titleBarPanelContent.add(styleBarPanel);
		addDragPanel();
		setStyleName("TitleBarPanel");
		addStyleName("TitleBarClassic");
		addStyleName("cursor_drag");
	}

	private void updateStyles() {
		titleBarPanelContent.setStyleName("TitleBarPanelContent",
					true);
	}

	private void initToggleButton() {
		// always show the view-icon; othrwise use showStylebar as parameter
		toggleStyleBarButton = new StandardButton(getToggleImage(false), null,
				32, 24);
		toggleStyleBarButton.addStyleName("toggleStyleBar");

		if (!dockPanel.showStyleBar && dockPanel.viewImage != null) {
			toggleStyleBarButton.addStyleName("toggleStyleBarViewIcon");
		}

		FastClickHandler toggleStyleBarHandler = source -> {
			dockPanel.setShowStyleBar(!dockPanel.showStyleBar);

			dockUpdateStyleBarVisibility();
			if (styleBar instanceof StyleBarW) {
				((StyleBarW) styleBar).setOpen(dockPanel.showStyleBar);
			}
		};
		toggleStyleBarButton.addFastClickHandler(toggleStyleBarHandler);
		titleBarPanelContent.add(toggleStyleBarButton);
	}

	protected void enableDragging(boolean drag) {
		if (dragPanel == null) {
			return;
		}

		// titleBarPanelContent is unvisible, when it's empty
		// so is has to be shown/hidden when dragmode changes
		titleBarPanelContent.setVisible(drag || !dockPanel.isStyleBarEmpty());
		dragPanel.setVisible(drag);

		// hide close button when in dragmode
		closeButtonPanel.setVisible(!drag);
		// TODO view menu?

		if (drag) {
			titleBarPanelContent.removeStyleName("TitleBarPanelContent");
			titleBarPanelContent.addStyleName("DragPanel");
		} else {
			titleBarPanelContent.removeStyleName("DragPanel");
			titleBarPanelContent.addStyleName("TitleBarPanelContent");
		}

		if (this.toggleStyleBarButton != null) {
			this.toggleStyleBarButton.setVisible(!drag);
		}
		if (drag) {
			this.styleBarPanel.setVisible(false);
		} else {
			dockUpdateStyleBarVisibility();
			if (styleBar instanceof StyleBarW) {
				((StyleBarW) styleBar).setOpen(dockPanel.showStyleBar);
			}
		}
	}

	private void addDragPanel() {
		if (titleBarPanelContent == null) {
			return;
		}
		if (dragPanel == null) {
			dragPanel = new FlowPanel();
			dragPanel.setStyleName("dragPanel");
			ClickStartHandler.init(dragPanel,
					new ClickStartHandler(true, false) {
						@Override
						public void onClickStart(int x, int y,
								PointerEventType type) {
							dockPanel.startDragging();
						}
					});
			dragPanel.setVisible(false);
			NoDragImage dragIcon = new NoDragImage(MaterialDesignResources.INSTANCE.move_canvas(),
					30);
			/*
			 * Prevent default image drag from interfering with view drag --
			 * needed for IE
			 */
			dragIcon.addDragHandler(DomEvent::preventDefault);

			dragPanel.add(dragIcon);
			titleBarPanelContent.add(dragPanel);
		}
	}

	private ResourcePrototype getToggleImage(boolean showing) {
		if (showing) {
			return GuiResources.INSTANCE.dockbar_triangle_right();
		}
		if (dockPanel.getViewIcon() != null) {
			return dockPanel.getViewIcon();
		}
		return GuiResources.INSTANCE.dockbar_triangle_left();
	}

	private void updateStyleBarVisibility() {
		if (app.isUnbundledOrWhiteboard() && toggleStyleBarButton != null) {
			if (!dockPanel.showStyleBar) {
				toggleStyleBarButton.setIcon(dockPanel.viewImage);
			}
		}

		styleBarPanel.setVisible(dockPanel.isStyleBarVisible());
		if (dockPanel.isStyleBarVisible()) {
			setStylebar();
			if (styleBar != null) {
				styleBar.setVisible(
						dockPanel.showStyleBar && !app.getGuiManager().isDraggingViews());
			}
		}
		if (styleBar instanceof SpreadsheetStyleBarW
				|| styleBar instanceof CASStylebarW
				|| styleBar instanceof AlgebraStyleBarW) {
			setStyleBarLongVisibility(dockPanel.isStyleBarVisible());
		}
	}

	protected void setCloseButtonVisible(boolean isVisible) {
		closeButtonPanel.setVisible(isVisible);
	}

	@Override
	public void setLabels() {
		if (toggleStyleBarButton != null) {
			toggleStyleBarButton
					.setTitle(app.getLocalization().getMenu("ToggleStyleBar"));
		}
	}

	private void setStylebar() {
		styleBar = dockPanel.loadStyleBar();
		styleBarPanel.add(styleBar);
	}

	/**
	 * Sets style bar visibility to true and false. When visible, style bar
	 * occupies a space of a full row (instead of floating). E.g. in CASView and
	 * SpreadsheetView
	 *
	 * @param value
	 *            true to show style bar
	 */
	private void setStyleBarLongVisibility(boolean value) {
		// in applets title bar may be null and view menu still enabled
		if (app.allowStylebar()
				&& getLayoutData() != null) {
			dockPanel.dockPanel.setWidgetSize(this, value ? 44 : 0);
			setStyleName("TitleBarPanel-open", value);
			dockPanel.setLongStyleBar(value);
			dockPanel.deferredOnResize();
		}
	}

	@Override
	public void setLayout() {
		if (dockPanel.isStyleBarVisible()) {
			dockPanel.buildGUIIfNecessary(false);
			setStylebar();
		}
		if (styleBar instanceof StyleBarW) {
			((StyleBarW) styleBar).setOpen(dockPanel.showStyleBar);
		}
		dockUpdateStyleBarVisibility();
	}

	/**
	 * Update the style bar visibility.
	 */
	public void dockUpdateStyleBarVisibility() {
		if (!dockPanel.isVisible()) {
			return;
		}
		dockPanel.buildGUIIfNecessary(true);
		updateStyleBarVisibility();
	}
}
