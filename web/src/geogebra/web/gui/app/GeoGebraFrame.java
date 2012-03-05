package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.SplashDialog;
import geogebra.web.helper.UrlFetcherImpl;
import geogebra.web.helper.XhrFactory;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.View;
import geogebra.web.main.Application;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main frame containing every view / menu bar / ....
 * This Panel (Frame is resize able)
 */
public class GeoGebraFrame extends VerticalPanel {

	private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
	private static GeoGebraFrame activeInstance;

	/** Loads file into active GeoGebraFrame */
	public static LoadFilePresenter fileLoader = new LoadFilePresenter(
			new UrlFetcherImpl(XhrFactory.getSupportedXhr(),
					GeoGebraConstants.URL_PARAM_GGB_FILE,
					GeoGebraConstants.URL_PARAM_PROXY,
					GeoGebraConstants.PROXY_SERVING_LOCATION));

	/** The application */
	protected Application app;

	private boolean resize = false;
	private boolean move = false;

	/** Creates new GeoGebraFrame */
	public GeoGebraFrame() {
		super();
		instances.add(this);
		activeInstance = this;
		DOM.sinkEvents(this.getElement(), Event.ONMOUSEDOWN | Event.ONMOUSEMOVE
				| Event.ONMOUSEUP | Event.ONMOUSEOVER);
	}

	/**
	 * Main entry points called by geogebra.web.Web.startGeoGebra()
	 * @param geoGebraMobileTags
	 *          list of &lt;article&gt; elements of the web page
	 */
	public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {
		init(geoGebraMobileTags);
	}

	private static void useDataParamBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null) if (dpBorder.length() == 7) if (dpBorder.charAt(0) == '#') {
			ae.getStyle().setBorderWidth(1, Style.Unit.PX);
			ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
			ae.getStyle().setBorderColor(dpBorder);
			gf.getStyleElement().getStyle().setBorderWidth(1, Style.Unit.PX);
			gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
			gf.getStyleElement().getStyle().setBorderColor(dpBorder);
		}
	}

	private static void init(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (ArticleElement articleElement : geoGebraMobileTags) {
			GeoGebraFrame inst = new GeoGebraFrame();
			Application app = inst.createApplication(articleElement);
			inst.app = app;
			inst.createSplash(articleElement);
			useDataParamBorder(articleElement, inst);
			inst.add(app.buildApplicationPanel());
			RootPanel.get(articleElement.getId()).add(inst);
			handleLoadFile(articleElement, app);

		}
	}
	
	public static void renderArticleElemnt(ArticleElement element) {
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		GeoGebraFrame inst = new GeoGebraFrame();
		Application app = inst.createApplication(element);
		inst.app = app;
		inst.createSplash(element);
		inst.add(app.buildApplicationPanel());
		RootPanel.get(element.getId()).add(inst);
		handleLoadFile(element, app);
	}

	private void createSplash(ArticleElement article) {
		this.app.splash = new SplashDialog();
		int splashWidth = 427;
		int splashHeight = 120;
		int width = article.getDataParamWidth();
		int height = article.getDataParamHeight();
		if (width > 0 && height > 0) {
			setWidth(width + "px");
			this.app.setDataParamWidth(width);
			this.app.setDataParamHeight(height);
			setHeight(height + "px");
			this.app.splash.addStyleName("splash");
			this.app.splash.getElement().getStyle()
					.setTop((height / 2) - (splashHeight / 2), Unit.PX);
			this.app.splash.getElement().getStyle()
					.setLeft((width / 2) - (splashWidth / 2), Unit.PX);

		}
		addStyleName("jsloaded");
		add(app.splash);
	}

	private static void handleLoadFile(ArticleElement articleElement,
			Application app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}

	/**
	 * @return the application
	 */
	public AbstractApplication getApplication() {
		return app;
	}

	/**
	 * Sets the Application of the GeoGebraFrame
	 * @param app
	 *          the application
	 */
	public void setApplication(Application app) {
		this.app = app;
	}

	/**
	 * @param useFullGui
	 *          if false only one euclidianView will be available (without
	 *          menus / ...)
	 * @return the newly created instance of Application
	 */
	protected Application createApplication(ArticleElement ae) {
		return new Application(ae);
	}

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if(!app.getUseFullGui()){
			return;
		}
		final int eventType = DOM.eventGetType(event);

		switch (eventType) {
		case Event.ONMOUSEOVER:
			if (isCursorAtResizePosition(event)) {
				DOM.setStyleAttribute(getElement(), "cursor", "se-resize");
			} else {
				DOM.setStyleAttribute(getElement(), "cursor", "default");
			}
			break;
		case Event.ONMOUSEDOWN:
			if (isCursorAtResizePosition(event)) {
				if (resize == false) {
					resize = true;
					DOM.setCapture(getElement());
				}
			}
			break;
		case Event.ONMOUSEMOVE:
			if (resize == true) {
				int width = DOM.eventGetClientX(event);
				int height = DOM.eventGetClientY(event);

				setPixelSize(width, height);
				app.getGuiManager().resize(width, height);
			} else if (move == true) {
				RootPanel.get().setWidgetPosition(this, DOM.eventGetClientX(event),
						DOM.eventGetClientY(event));
			}
			break;
		case Event.ONMOUSEUP:
			if (move == true) {
				move = false;
				DOM.releaseCapture(getElement());
			}
			if (resize == true) {
				resize = false;
				DOM.releaseCapture(getElement());
			}
			break;
		}
	}

	/**
	 * Determines if the mouse cursor is at the bottom right position of the Frame
	 * @param event
	 *          the cursor event
	 * @return true if cursor is within a 10x10 frame on the bottom right position
	 */
	protected boolean isCursorAtResizePosition(Event event) {
		int cursorY = DOM.eventGetClientY(event);
		int initialY = getAbsoluteTop();
		int height = getOffsetHeight();

		int cursorX = DOM.eventGetClientX(event);
		int initialX = getAbsoluteLeft();
		int width = getOffsetWidth();

		if (((initialX + width - 10) < cursorX && cursorX <= (initialX + width))
				&& ((initialY + height - 10) < cursorY && cursorY <= (initialY + height))) {
			return true;
		}
		return false;
	}
	
}
