package geogebra.mobile.gui;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.mobile.controller.MobileAlgebraController;
import geogebra.mobile.controller.MobileEuclidianController;
import geogebra.mobile.gui.elements.AlgebraViewPanel;
import geogebra.mobile.gui.elements.EuclidianViewPanel;
import geogebra.mobile.gui.elements.GuiModel;
import geogebra.mobile.gui.elements.TabletHeaderPanel;
import geogebra.mobile.gui.elements.TabletHeaderPanelLeft;
import geogebra.mobile.gui.elements.TabletHeaderPanelRight;
import geogebra.mobile.gui.elements.toolbar.ToolBar;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.Button;

public class TabletGUI implements GeoGebraMobileGUI
{
	private EuclidianViewPanel euclidianViewPanel;
	private TabletHeaderPanel headerPanel;
	private TabletHeaderPanelLeft leftHeader;
	private TabletHeaderPanelRight rightHeader;
	private AlgebraViewPanel algebraViewPanel;
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
		this.headerPanel = new TabletHeaderPanel();
		this.leftHeader = new TabletHeaderPanelLeft();
		this.rightHeader = new TabletHeaderPanelRight();
		this.toolBar = new ToolBar();
		this.euclidianViewPanel = new EuclidianViewPanel();
		this.algebraViewPanel = new AlgebraViewPanel();

		layout();
	}

	public void layout()
	{
		RootPanel.get().add(this.euclidianViewPanel);

		RootPanel.get().add(this.headerPanel);
		RootPanel.get().add(this.rightHeader);
		RootPanel.get().add(this.leftHeader);

		this.toolBar.makeTabletToolBar(new GuiModel());
		RootPanel.get().add(this.toolBar);

		RootPanel.get().add(this.algebraViewPanel);
	}

	@Override
	public EuclidianViewPanel getEuclidianViewPanel()
	{
		return this.euclidianViewPanel;
	}

	@Override
	public AlgebraViewPanel getAlgebraViewPanel()
	{
		return this.algebraViewPanel;
	}

	@Override
	public void initComponents(final Kernel kernel)
	{
		// TODO add other stuff
		MobileEuclidianController ec = new MobileEuclidianController();
		ec.setKernel(kernel);
		this.euclidianViewPanel.initEuclidianView(ec);

		MobileAlgebraController ac = new MobileAlgebraController(kernel);
		this.algebraViewPanel.initAlgebraView(ac, kernel);
	}
}
