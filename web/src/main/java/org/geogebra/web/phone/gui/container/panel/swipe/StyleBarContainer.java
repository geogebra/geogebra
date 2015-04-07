package org.geogebra.web.phone.gui.container.panel.swipe;

import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.phone.gui.view.StyleBar;
import org.geogebra.web.web.gui.util.StandardButton;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class StyleBarContainer extends FlowPanel implements FastClickHandler {
	
	private StyleBar styleBar;
	private boolean showStyleBar = false;
	
	private Panel styleBarPanel;
	
	public StyleBarContainer(StyleBar styleBar) {
		this.styleBar = styleBar;
		setStyleName("TitleBarPanel");
		buildGui();
	}
	
	private void buildGui() {
		FlowPanel titleBarPanelContent = new FlowPanel();
		titleBarPanelContent.setStyleName("TitleBarPanelContent");
		add(titleBarPanelContent);
		
		Widget styleBarWidget = styleBar.getStyleBar().asWidget();
		
		styleBarPanel = new FlowPanel();
		styleBarPanel.setStyleName("StyleBar_");
		styleBarPanel.add(styleBarWidget);
		
		titleBarPanelContent.add(styleBarPanel);
		
		StandardButton toggleStyleBarButton = createToggleButton();
		titleBarPanelContent.add(toggleStyleBarButton);

		// close stylebar
		showStyleBar = false;
		styleBar.setOpen(showStyleBar);
		styleBarPanel.setVisible(showStyleBar);
		styleBar.getStyleBar().asWidget().setVisible(showStyleBar);
	}
	
	private StandardButton createToggleButton() {
		StandardButton toggleButton = new StandardButton(styleBar.getStyleBarIcon(), null, 32);
		toggleButton.addStyleName("toggleStyleBar");
		toggleButton.addStyleName("toggleStyleBarViewIcon");
		toggleButton.addFastClickHandler(this);
		
		return toggleButton;
	}
	
	private void setShowStyleBar(boolean value) {
		if (showStyleBar == value) {
			return;
		}
		showStyleBar = value;
		styleBar.setOpen(showStyleBar);
		styleBarPanel.setVisible(showStyleBar);
		styleBar.getStyleBar().asWidget().setVisible(showStyleBar);
	}

	private void toggleStyleBar() {
		setShowStyleBar(!showStyleBar);
	}

	public void onClick(Widget source) {
		toggleStyleBar();
	}

	
}
