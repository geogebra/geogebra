package geogebra.mobile.gui;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanel;
import geogebra.mobile.gui.elements.TabletToolBar;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

public class TabletGUI implements GeoGebraMobileGUI
{
	private RootPanel rootPanel;

	private LayoutPanel euclidianViewPanel;
	private HeaderPanel headerPanel;
	private LayoutPanel algebraViewPanel;
	private ButtonBar toolBar;

	public TabletGUI()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		// this will create a link element at the end of head
		MGWTStyle.getTheme().getMGWTClientBundle().getMainCss().ensureInjected();

		// append your own css as last thing in the head
		MGWTStyle.injectStyleSheet("TabletGUI.css");

		// Initialize GUI Elements
		rootPanel = RootPanel.get();
		headerPanel = new HeaderPanel();
		toolBar = new TabletToolBar();

		layout();
	}

	public void layout()
	{
		rootPanel = RootPanel.get();

		headerPanel = new TabletHeaderPanel();
		euclidianViewPanel = new EuclidianViewPanel();
		algebraViewPanel = new AlgebraViewPanel();

		euclidianViewPanel.add(headerPanel);
		euclidianViewPanel.add(algebraViewPanel);

		euclidianViewPanel.add(toolBar);
		rootPanel.add(euclidianViewPanel);
	}
}
