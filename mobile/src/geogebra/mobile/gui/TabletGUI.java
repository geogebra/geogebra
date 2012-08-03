package geogebra.mobile.gui;

import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.toolbar.ToolBar;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.LayoutPanel;

public class TabletGUI implements GeoGebraMobileGUI
{
	private RootPanel rootPanel;

	private LayoutPanel euclidianViewPanel;
	private TabletHeaderPanel headerPanel;
	private TabletHeaderPanelLeft leftHeader; 
	private TabletHeaderPanelRight rightHeader; 	
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
		this.rootPanel = RootPanel.get();
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft(); 
		this.rightHeader = new TabletHeaderPanelRight(); 
		this.toolBar = new ToolBar();

		layout();
	}

	public void layout()
	{
		this.rootPanel = RootPanel.get();

		this.headerPanel = new TabletHeaderPanel();
		this.euclidianViewPanel = new EuclidianViewPanel();
		this.algebraViewPanel = new AlgebraViewPanel();

		this.euclidianViewPanel.add(this.headerPanel);
		this.euclidianViewPanel.add(this.rightHeader);
		this.euclidianViewPanel.add(this.leftHeader);
		
		this.euclidianViewPanel.add(this.algebraViewPanel);

		this.toolBar.makeTabletToolBar();
		this.euclidianViewPanel.add(this.toolBar);
		this.rootPanel.add(this.euclidianViewPanel);
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
