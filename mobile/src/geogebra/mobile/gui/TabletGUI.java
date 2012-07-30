package geogebra.mobile.gui;

import geogebra.common.main.App;
import geogebra.mobile.MobileApp;

import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBarButtonBase;

public class TabletGUI implements GeoGebraMobileGUI
{
	private App app;

	private RootPanel root;

	private HeaderPanel headerPanel;
	private ButtonBar toolBar;

	public TabletGUI()
	{
		root = RootPanel.get();

		headerPanel = new HeaderPanel();
		toolBar = new ButtonBar();
	}

	public void start()
	{
		// set viewport and other settings for mobile
		MGWT.applySettings(MGWTSettings.getAppSetting());

		headerPanel.addStyleName("headerpanel");

		headerPanel.setTitle("Title");
		headerPanel.setCenter("Title");

		toolBar.addStyleName("toolbar");

		ButtonBarButtonBase[] b = new ButtonBarButtonBase[10];
		for (int i = 0; i < 10; i++)
		{
			b[i] = new ButtonBarButtonBase(Resources.INSTANCE.logo());
			b[i].getElement().getStyle().setBackgroundImage(Resources.INSTANCE.tux().getSafeUri().asString());
			b[i].setTitle("bla" + i);
			b[i].addStyleName("toolbutton" + i);
			toolBar.add(b[i]);
		}

		// Initialize the AppW app
		app = new MobileApp();

		root.add(headerPanel);
		// TODO! Get the EuclidianView into a panel outside of App
		// root.add(app.getEuclidianViewpanel());

		root.add(toolBar);

		App.debug("I'm here!");

	}
}
