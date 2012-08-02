package geogebra.mobile.gui;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanel;
import geogebra.mobile.gui.elements.ToolBar;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;


public class TabletGUI implements GeoGebraMobileGUI
{
	private RootPanel rootPanel;

	private LayoutPanel euclidianViewPanel;
	private HeaderPanel headerPanel;
	private LayoutPanel algebraViewPanel;
	private ToolBar toolBar;

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
		toolBar = new ToolBar();

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

		toolBar.makeTabletToolBar();
		euclidianViewPanel.add(toolBar);
		rootPanel.add(euclidianViewPanel);
	}

	@Override
  public EuclidianViewPanel getEuclidianViewPanel()
  {
	  // TODO Auto-generated method stub
	  return null;
  }

	@Override
  public AlgebraViewPanel getAlgebraViewPanel()
  {
	  // TODO Auto-generated method stub
	  return null;
  }
}
