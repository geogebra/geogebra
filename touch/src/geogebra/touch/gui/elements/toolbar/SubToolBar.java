package geogebra.touch.gui.elements.toolbar;

import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.ResizeListener;
import geogebra.touch.gui.TabletGUI;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Each {@link ToolBarButton ToolBarButton} has its own options.
 * 
 * @author Thomas Krismayer
 * @see ButtonBar
 */
public class SubToolBar extends PopupPanel implements ResizeListener {
	private final VerticalPanel contentPanel;
	private CellPanel subToolBarPanel;
	private final LayoutPanel arrowPanel;
	private boolean horizontal;

	/**
	 * @param menuEntry
	 *            the SubToolBarButtons that will be shown
	 */
	public SubToolBar(SubToolBarButton[] menuEntry, TouchApp app) {
		this.setStyleName("subToolBar");

		this.contentPanel = new VerticalPanel();

		if (Window.getClientWidth() < 600) {
			this.subToolBarPanel = new VerticalPanel();
			this.horizontal = false;
		} else {
			this.subToolBarPanel = new HorizontalPanel();
			this.horizontal = true;
		}

		this.subToolBarPanel.setStyleName("subToolBarButtonPanel");

		for (SubToolBarButton b : menuEntry) {
			this.subToolBarPanel.add(b);
		}

		this.contentPanel.add(this.subToolBarPanel);
		this.setWidget(this.contentPanel);

		this.arrowPanel = new LayoutPanel();
		final String html = "<img src=\""
				+ TouchEntryPoint.getLookAndFeel().getIcons().subToolBarArrow()
						.getSafeUri().asString() + "\" />";
		this.arrowPanel.getElement().setInnerHTML(html);
		this.contentPanel.add(this.arrowPanel);
		this.arrowPanel.setStyleName("subToolBarArrow");
		
		((TabletGUI) app.getTouchGui()).addResizeListener(this);
	}

	public void setSubToolBarArrowPaddingLeft(int padding) {
		this.arrowPanel.getElement().setAttribute("style",
				"padding-left: " + padding + "px;");
	}

	public boolean isHorizontal() {
		return this.horizontal;
	}

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		
	}
}
