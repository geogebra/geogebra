package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.ToolbarMow.TabIds;
import org.geogebra.web.full.gui.util.ToggleButton;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.zoompanel.FocusableWidget;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Similar toolbar header as for graphing
 */
public class HeaderMow extends FlowPanel
		implements FastClickHandler, SetLabels {
	private AppW appW;
	private ToolbarMow toolbar;
	private FlowPanel content;
	private FlowPanel center;
	private StandardButton penPanelBtn;
	private StandardButton toolsPanelBtn;
	private StandardButton mediaPanelBtn;
	private ToggleButton openCloseBtn;

	/**
	 * constructor
	 * 
	 * @param toolbar
	 *            mow toolbar
	 * @param app
	 *            see {@link AppW}
	 */
	public HeaderMow(ToolbarMow toolbar, AppW app) {
		this.appW = app;
		this.toolbar = toolbar;
		initGui();
	}

	private void initGui() {
		addStyleName("headerMow");
		content = new FlowPanel();
		content.addStyleName("content");
		createCenter();
		createRight();
		new FocusableWidget(AccessibilityGroup.NOTES_TOOLBAR_HEADER, null,
				penPanelBtn, toolsPanelBtn, mediaPanelBtn, openCloseBtn).attachTo(appW);
		add(content);
	}

	private void createCenter() {
		center = new FlowPanel();
		center.addStyleName("center");
		center.addStyleName("indicatorLeft");
		Element indicator = DOM.createDiv();
		indicator.addClassName("indicator");
		center.getElement().insertFirst(indicator);
		penPanelBtn = createButton(
				MaterialDesignResources.INSTANCE.mow_pen_panel(), "Pen");
		penPanelBtn.addStyleName("flatButton");
		toolsPanelBtn = createButton(
				MaterialDesignResources.INSTANCE.toolbar_tools().withFill(GColor.WHITE.toString()),
				"Tools");
		TestHarness.setAttr(toolsPanelBtn, "toolsPanelButton");
		toolsPanelBtn.addStyleName("flatButton");
		TestHarness.setAttr(toolsPanelBtn, "toolsPanelButton");
		mediaPanelBtn = createButton(
				MaterialDesignResources.INSTANCE.mow_media_panel(),
				"ToolCategory.Media");
		mediaPanelBtn.addStyleName("flatButton");
		TestHarness.setAttr(mediaPanelBtn, "mediaPanelButton");
		center.add(penPanelBtn);
		center.add(toolsPanelBtn);
		center.add(mediaPanelBtn);
		content.add(center);
	}

	private StandardButton createButton(SVGResource resource, String tooltip) {
		StandardButton button = new StandardButton(resource, null, 24);
		button.setTitle(appW.getLocalization().getMenu(tooltip));
		button.addFastClickHandler(this);
		return button;
	}

	private void createRight() {
		openCloseBtn = new ToggleButton(MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_white(), MaterialDesignResources.INSTANCE
				.toolbar_open_portrait_white());
		openCloseBtn.setStyleName("flatButton");
		openCloseBtn.addStyleName("button");
		openCloseBtn.addStyleName("openCloseBtn");
		AriaHelper.setTitle(openCloseBtn, appW.getLocalization().getMenu("Close"));
		openCloseBtn.addFastClickHandler(event -> {
				onOpenClose();
				DOM.setCapture(null);
		});
		content.add(openCloseBtn);
	}

	@Override
	public void onClick(Widget source) {
		if (source == penPanelBtn) {
			tabSwitch(TabIds.PEN);
		} else if (source == toolsPanelBtn) {
			tabSwitch(TabIds.TOOLS);
		} else if (source == mediaPanelBtn) {
			tabSwitch(TabIds.MEDIA);
		}
	}

	private void tabSwitch(TabIds tab) {
		center.setStyleName("center");
		switch (tab) {
		case PEN:
			center.addStyleName("indicatorLeft");
			break;
		case TOOLS:
			center.addStyleName("indicatorCenter");
			break;
		case MEDIA:
			center.addStyleName("indicatorRight");
			break;
		default:
			break;
		}
		if (!toolbar.isOpen()) {
			openCloseBtn.setIcon(MaterialDesignResources.INSTANCE
					.toolbar_close_portrait_white());
			openCloseBtn.setSelected(false);
			onOpenClose();
		}
		toolbar.tabSwitch(tab);
	}

	/**
	 * on open/close toolbar
	 */
	public void onOpenClose() {
		toggleCloseButton(toolbar.isOpen());
		toolbar.onOpenClose();
	}

	/**
	 * Toggles the open/close icon for open/close button
	 * @param open true if toolbar is open
	 */
	public void toggleCloseButton(boolean open) {
		AriaHelper.setTitle(openCloseBtn, appW.getLocalization()
						.getMenu(open ? "Open" : "Close"));
	}

	@Override
	public void setLabels() {
		penPanelBtn.setTitle(appW.getLocalization().getMenu("Pen"));
		toolsPanelBtn.setTitle(appW.getLocalization().getMenu("Tools"));
		mediaPanelBtn
				.setTitle(appW.getLocalization().getMenu("ToolCategory.Media"));
		AriaHelper.setTitle(openCloseBtn, appW.getLocalization()
				.getMenu(toolbar.isOpen() ? "Open" : "Close"));
	}
}
