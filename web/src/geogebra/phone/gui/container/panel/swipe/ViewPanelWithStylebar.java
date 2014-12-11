package geogebra.phone.gui.container.panel.swipe;

import geogebra.html5.gui.ResizeListener;
import geogebra.phone.gui.view.StyleBarPanel;
import geogebra.phone.gui.view.ViewPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A panel containing a view and a stylebar in the top right corner.
 */
public class ViewPanelWithStylebar extends AbsolutePanel implements
		ResizeListener {

	private ViewPanel panel;
	private StyleBarPanel styleBar;

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
		this.panel = panel;
		add(panel);
		refreshPanel();
	}

	/**
	 * Sets the style bar.
	 * 
	 * @param styleBar the style bar
	 */
	public void setStyleBar(StyleBarPanel styleBar) {
		if (styleBar == null) {
			return;
		}
		this.styleBar = styleBar;
		add(styleBar);
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
		Widget styleBarWidget = styleBar.asWidget();
		styleBarWidget.setHeight("40px");
		styleBarWidget.setWidth("40px");
		Style styleBarStyle = styleBarWidget.getElement().getStyle();
		styleBarStyle.setRight(0, Unit.PX);
		styleBarStyle.setZIndex(1);
	}
}
