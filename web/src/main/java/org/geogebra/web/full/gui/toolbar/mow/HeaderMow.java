package org.geogebra.web.full.gui.toolbar.mow;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.toolbar.mow.ToolbarMow.TabIds;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.MyToggleButton;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Similar toolbar header as for graphing
 * 
 * @author csilla
 *
 */
public class HeaderMow extends FlowPanel
		implements FastClickHandler {
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
		center.getElement().setInnerHTML(center.getElement().getInnerHTML()
				+ "<div class=\"indicator\"></div>");
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
		openCloseBtn = new MyToggleButton(appW);
		openCloseBtn.setUpfaceDownfaceImg(
				MaterialDesignResources.INSTANCE.toolbar_open_portrait_white(),
				MaterialDesignResources.INSTANCE
						.toolbar_close_portrait_white());
		openCloseBtn.setStyleName("flatButton button");
		openCloseBtn.addStyleName("openCloseBtn");
		openCloseBtn.setTitle(appW.getLocalization().getMenu("Open"));
		content.add(openCloseBtn);
	}

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
		toolbar.tabSwitch(tab);
	}
}
