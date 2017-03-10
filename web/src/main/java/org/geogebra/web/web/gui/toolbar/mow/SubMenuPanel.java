package org.geogebra.web.web.gui.toolbar.mow;

import java.util.Vector;

import org.geogebra.common.gui.toolbar.ToolBar;
import org.geogebra.common.gui.toolbar.ToolbarItem;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.images.ImgResourceHelper;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class SubMenuPanel extends FlowPanel implements ClickHandler, FastClickHandler {
	AppW app;
	private boolean info;
	ScrollPanel scrollPanel;
	FlowPanel contentPanel;
	FlowPanel infoPanel;

	NoDragImage infoImage;
	NoDragImage questionMark;
	HTML infoLabel;
	String infoURL;

	public SubMenuPanel(AppW app, boolean info) {
		this.app=app;
		this.info = info;
		createGUI();
	}

	protected void createGUI() {
		addStyleName("mowSubMenu");
		createContentPanel();
		if (hasInfo()) {
			createInfoPanel();
			add(LayoutUtilW.panelRow(scrollPanel, infoPanel));
		} else {
			add(scrollPanel);
		}
	}

	protected void createContentPanel() {
		scrollPanel = new ScrollPanel();
		scrollPanel.addStyleName("mowSubMenuContent");
		contentPanel = new FlowPanel();
		scrollPanel.add(contentPanel);
	}

	protected void createInfoPanel() {
		infoPanel = new FlowPanel();
		infoPanel.addStyleName("mowSubMenuInfo");
	}

	protected Vector<ToolbarItem> getToolbarVec(String toolbarString) {
		Vector<ToolbarItem> toolbarVec;
		try {

			toolbarVec = ToolBar.parseToolbarString(toolbarString);
			Log.debug("toolbarVec parsed");

		} catch (Exception e) {

			Log.debug("invalid toolbar string: " + toolbarString);

			toolbarVec = ToolBar.parseToolbarString(ToolBar.getAllTools(app));
		}
		return toolbarVec;
	}

	protected void addModesToToolbar(String toolbarString) {
		addModesToToolbar(contentPanel, toolbarString);
	}

	protected void addModesToToolbar(FlowPanel panel, String toolbarString) {
		Vector<ToolbarItem> toolbarVec = getToolbarVec(toolbarString);
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();
			addModeMenu(panel, menu);
		}
	}

	protected void addModeMenu(FlowPanel panel, Vector<Integer> menu) {
		if (app.isModeValid(menu.get(0).intValue())) {
			panel.add(createButton(menu.get(0).intValue()));
		}
	}

	protected StandardButton createButton(int mode) {
		NoDragImage im = new NoDragImage(GGWToolBar.getImageURL(mode, app));
		StandardButton button = new StandardButton(null, "", 32);
		button.getUpFace().setImage(im);
		button.addFastClickHandler(this);

		button.addStyleName("mowToolButton");
		button.getElement().setAttribute("mode", mode + "");
		return button;
	}

	public boolean hasInfo() {
		return info;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}

	public void onOpen() {
		deselectAllCSS();
		infoPanel.clear();
	}

	@Override
	public void onClick(Widget source) {
		int mode = Integer.parseInt(source.getElement().getAttribute("mode"));
		setCSStoSelected(source);
		app.setMode(mode);
		if (hasInfo()) {
			infoPanel.clear();
			showToolTip(mode);
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == questionMark) {
			app.getFileManager().open(infoURL);
		}
	}

	public void setCSStoSelected(Widget source) {

		FlowPanel parent = (FlowPanel) source.getParent();
		for (int i = 0; i < parent.getWidgetCount(); i++) {
			Widget w = parent.getWidget(i);
			if (w != source) {
				w.getElement().setAttribute("selected", "false");
			} else {
				w.getElement().setAttribute("selected", "true");
			}
		}
	}

	public void deselectAllCSS() {
	}


	protected void showToolTip(int mode) {
		if (mode >= 0) {
			infoImage = new NoDragImage(GGWToolBar.getImageURL(mode, app));
			infoImage.addStyleName("mowToolButton");

			infoLabel = new HTML(app.getToolTooltipHTML(mode));
			infoLabel.addStyleName("mowInfoLabel");
			infoURL = app.getGuiManager().getTooltipURL(mode);
			questionMark = new NoDragImage(ImgResourceHelper.safeURI(GGWToolBar.getMyIconResourceBundle().help_32()));
			infoPanel.add(infoImage);
			infoPanel.add(infoLabel);

			boolean online = app.getNetworkOperation() == null || app.getNetworkOperation().isOnline();
			if (infoURL != null && infoURL.length() > 0 && online) {
				questionMark.addClickHandler(this);
				questionMark.addStyleName("mowQuestionMark");
				infoPanel.add(questionMark);
			}
		}
	}

}
