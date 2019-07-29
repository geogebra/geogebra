package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.ToolbarMow.TabIds;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.view.button.MyToggleButton;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Similar toolbar header as for graphing
 * 
 * @author csilla
 *
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
	private MyToggleButton openCloseBtn;

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
				MaterialDesignResources.INSTANCE.toolbar_tools(), "Tools");
		toolsPanelBtn.addStyleName("flatButton");
		mediaPanelBtn = createButton(
				MaterialDesignResources.INSTANCE.mow_media_panel(),
				"ToolCategory.Media");
		mediaPanelBtn.addStyleName("flatButton");
		center.add(penPanelBtn);
		center.add(toolsPanelBtn);
		center.add(mediaPanelBtn);
		content.add(center);
	}

	private StandardButton createButton(SVGResource resource, String tooltip) {
		StandardButton button = new StandardButton(resource, null, 24, appW);
		button.setTitle(appW.getLocalization().getMenu(tooltip));
		button.addFastClickHandler(this);
		return button;
	}

	private void createRight() {
		openCloseBtn = new MyToggleButton(new Image(
				getIcon(MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_white())),
				appW);
		openCloseBtn.setStyleName("flatButton");
		openCloseBtn.addStyleName("button");
		openCloseBtn.addStyleName("openCloseBtn");
		openCloseBtn.setTitle(appW.getLocalization().getMenu("Close"));
		ClickStartHandler.init(openCloseBtn, new ClickStartHandler(true, true) {
			
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				onOpenClose();
			}
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
			onOpenClose();
		}
		toolbar.tabSwitch(tab);
	}

	/**
	 * on open/close toolbar
	 */
	public void onOpenClose() {
		toolbar.getFrame().deselectDragBtn();
		toolbar.setStyleName(
				toolbar.isOpen() ? "hideMowToolbarPanel"
						: "showMowToolbarPanel");
		toggleCloseButton();
		toolbar.setOpen(!toolbar.isOpen());
		toolbar.addStyleName("toolbarMow");
		toolbar.updateFloatingButtonsPosition();
	}

	/**
	 * Toggles the open/close icon for open/close button
	 */
	public void toggleCloseButton() {
		Image upFace = new Image(getIcon(MaterialDesignResources.INSTANCE
				.toolbar_open_portrait_white()));
		upFace.getElement().setAttribute("draggable", "false");
		Image downFace = new Image(getIcon(MaterialDesignResources.INSTANCE
				.toolbar_close_portrait_white()));
		downFace.getElement().setAttribute("draggable", "false");
		openCloseBtn.getUpFace().setImage(toolbar.isOpen() ? upFace : downFace);
		openCloseBtn.setTitle(
				appW.getLocalization()
						.getMenu(toolbar.isOpen() ? "Open" : "Close"));
	}

	/**
	 * @param resource
	 *            svg source
	 * @return image resource
	 */
	public static ImageResourcePrototype getIcon(SVGResource resource) {
		return new ImageResourcePrototype(null, resource.getSafeUri(), 0, 0, 24,
				24, false, false);
	}

	@Override
	public void setLabels() {
		penPanelBtn.setTitle(appW.getLocalization().getMenu("Pen"));
		toolsPanelBtn.setTitle(appW.getLocalization().getMenu("Tools"));
		mediaPanelBtn
				.setTitle(appW.getLocalization().getMenu("ToolCategory.Media"));
		openCloseBtn.setTitle(appW.getLocalization()
				.getMenu(toolbar.isOpen() ? "Open" : "Close"));
	}
}
