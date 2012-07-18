package geogebra.mobile.gui;

import geogebra.mobile.MobileApp;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

public class GeoGebraMobileFrame extends GeoGebraFrame
{
	private AppW app;
	private ArticleElement element;

	private RootPanel root;

	private Canvas euclidianCanvas;

	private HeaderPanel headerPanel;
	private ButtonBar toolBar;

	public GeoGebraMobileFrame()
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

		Button[] b = new Button[10];
		for (int i = 0; i < 10; i++)
		{
			b[i] = new Button();
			b[i].setText("bla" + i);
			b[i].addStyleName("toolbutton" + i);
			toolBar.add(b[i]);
		}

		// get the article element from Mobile.html)
		element = ArticleElement.as(Dom.querySelector("geogebramobile"));
		
		// Initialize the AppW app
		app = new MobileApp(element, this); 

		// Get the Canvas
		euclidianCanvas = app.getCanvas();
		
		root.add(headerPanel);
		root.add(toolBar);
		
		// Add the canvas to the rootPanel
		// What do i do wrong?
		root.add(euclidianCanvas);
	}
}
