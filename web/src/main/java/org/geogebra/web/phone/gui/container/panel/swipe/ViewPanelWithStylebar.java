package org.geogebra.web.phone.gui.container.panel.swipe;

import org.geogebra.web.html5.gui.ResizeListener;
import org.geogebra.web.phone.gui.view.StyleBar;
import org.geogebra.web.phone.gui.view.ViewPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel containing a view and a stylebar in the top right corner.
 */
public class ViewPanelWithStylebar extends AbsolutePanel implements
		ResizeListener {

	private ViewPanel panel;
	
	private StyleBar styleBar;
	private StyleBarContainer styleBarContainer;

	public void onResize() {
		refreshPanel();
		refreshStyleBar();
	}

	/**
	 * Sets the view panel.
	 * 
	 * @param panel the panel
	 */
	public void setPanel(ViewPanel panel) {
		if (panel == null) {
			return;
		}
		if (this.panel != null) {
			remove(this.panel);
		}
		this.panel = panel;
		add(panel);
		refreshPanel();
	}

	/**
	 * Sets the style bar.
	 * 
	 * @param styleBar the style bar
	 */
	public void setStyleBar(StyleBar styleBar) {
		if (styleBar == null) {
			return;
		}
		if (this.styleBar != null) {
			remove(this.styleBarContainer);
		}
		this.styleBar = styleBar;
		this.styleBarContainer = new StyleBarContainer(styleBar);
		add(styleBarContainer);
		refreshStyleBar();
	}

	private void refreshPanel() {
		if (panel == null) {
			return;
		}
		Style style = getElement().getStyle();
		String height = style.getHeight();
		String width = style.getWidth();

		Widget panelWidget = panel.asWidget();
		panelWidget.setHeight(height);
		panelWidget.setWidth(width);
		panel.onResize();
	}

	private void refreshStyleBar() {
		if (styleBar == null) {
			return;
		}
		Widget styleBarWidget = styleBarContainer;
		//styleBarWidget.setHeight("40px");
		//styleBarWidget.setWidth("40px");
		Style styleBarStyle = styleBarWidget.getElement().getStyle();
		styleBarStyle.setRight(0, Unit.PX);
		styleBarStyle.setTop(0, Unit.PX);
		styleBarStyle.setPosition(Position.ABSOLUTE);
		styleBarStyle.setZIndex(1);
	}
}
