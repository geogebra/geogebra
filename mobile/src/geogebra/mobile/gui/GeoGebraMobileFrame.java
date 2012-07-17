package geogebra.mobile.gui;

import geogebra.common.euclidian.EuclidianController;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.main.settings.EuclidianSettings;
import geogebra.common.main.settings.Settings;
import geogebra.mobile.MobileApp;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.euclidian.EuclidianViewW;
import geogebra.web.gui.applet.GeoGebraFrame;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.Dom;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.widget.Button;
import com.googlecode.mgwt.ui.client.widget.HeaderPanel;
import com.googlecode.mgwt.ui.client.widget.buttonbar.ButtonBar;

public class GeoGebraMobileFrame extends GeoGebraFrame
{
	// interface MyUiBinder extends UiBinder<LayoutPanel, GeoGebraMobileFrame>
	// {
	// }

	// private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	// @UiField
	EuclidianView euclidianView;
	EuclidianController controller;
	EuclidianSettings settings;
	Kernel kernel;
	App app;
	ArticleElement element;

	private RootPanel root;

	private AbsolutePanel euclidianPanel;

	private HeaderPanel headerPanel;
	private ButtonBar toolBar;

	public GeoGebraMobileFrame()
	{
		root = RootPanel.get();

		headerPanel = new HeaderPanel();
		toolBar = new ButtonBar();

		euclidianPanel = new AbsolutePanel();

		element = ArticleElement.as(Dom.querySelector("geogebramobile"));

		app = new MobileApp(element, this, true);
		kernel = new Kernel(app);
		controller = new EuclidianControllerW(kernel);
		settings = new EuclidianSettings(new Settings().getEuclidian(1));

		Canvas canvas = Canvas.createIfSupported();
		euclidianPanel.add(canvas); 
		
		euclidianView = new EuclidianViewW(euclidianPanel, controller, new boolean[] { true, true }, true, settings);

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
			b[i].addStyleName("toolbutton");
			toolBar.add(b[i]);
		}

		root.add(headerPanel);
		root.add(toolBar);
		root.add(euclidianPanel);
	}
}
