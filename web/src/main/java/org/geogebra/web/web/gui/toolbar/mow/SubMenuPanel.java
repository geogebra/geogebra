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
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class SubMenuPanel extends FlowPanel implements ClickHandler, FastClickHandler {
	AppW app;
	private boolean info;
	ScrollPanel scrollPanel;
	FlowPanel contentPanel;
	FlowPanel infoPanel;

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
		Vector<ToolbarItem> toolbarVec = getToolbarVec(toolbarString);
		for (int i = 0; i < toolbarVec.size(); i++) {
			ToolbarItem ob = toolbarVec.get(i);
			Vector<Integer> menu = ob.getMenu();

			if (app.isModeValid(menu.get(0).intValue())) {
				addButton(menu.get(0).intValue());
			}
		}
	}

	protected void addButton(int mode) {
		NoDragImage im = new NoDragImage(GGWToolBar.getImageURL(mode, app));
		StandardButton button = new StandardButton(null, "", 32);
		button.getUpFace().setImage(im);
		button.addFastClickHandler(this);

		button.addStyleName("mowToolButton");
		button.getElement().setAttribute("mode", mode + "");
		contentPanel.add(button);
	}

	public boolean hasInfo() {
		return info;
	}

	public void setInfo(boolean info) {
		this.info = info;
	}

	public void onOpen() {

	}

	@Override
	public void onClick(Widget source) {
		int mode = Integer.parseInt(source.getElement().getAttribute("mode"));
		setCSStoSelected(source.getElement());
		app.setMode(mode);

	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	public void setCSStoSelected(Element e) {
		for (int i = 0; i < contentPanel.getWidgetCount(); i++) {
			Element w = contentPanel.getWidget(i).getElement();
			if (w != e) {
				w.setAttribute("selected", "false");
			} else {
				w.setAttribute("selected", "true");
			}
		}
	}
}
