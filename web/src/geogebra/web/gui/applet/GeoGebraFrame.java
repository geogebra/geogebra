package geogebra.web.gui.applet;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.web.gui.SplashDialog;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.View;
import geogebra.web.js.ResourcesInjector;
import geogebra.web.main.AppW;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
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
	public static LoadFilePresenter fileLoader = new LoadFilePresenter();

	/** The application */
	protected AppW app;

	private boolean resize = false;
	private boolean move = false;
	private String customToolbar;

	/**
	 * Splash Dialog to get it work quickly
	 */
	public SplashDialog splash;

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
	
	/* 
	 * In the splashDialog onLoad handler will the application loading continue
	 */
	
	private void createSplash(ArticleElement ae) {
		int splashWidth = 427;
		int splashHeight = 120;
		int width = ae.getDataParamWidth();
		int height = ae.getDataParamHeight();
		if (ae.getDataParamShowMenuBar()) {
			// The menubar has extra height:
			height += 31;
		}
		if (ae.getDataParamShowToolBar()) {
			// The toolbar has extra height:
			height += 57;
		}
		boolean showLogo = ((width >= splashWidth) && (height >= splashHeight));
		splash = new SplashDialog(showLogo);
		
		if (width > 0 && height > 0) {
			setWidth(width + "px");
			setDataParamWidth(ae.getDataParamWidth());
			setDataParamHeight(ae.getDataParamHeight());
			setHeight(height + "px");
			splash.addStyleName("splash");
			splash.getElement().getStyle()
			        .setTop((height / 2) - (splashHeight / 2), Unit.PX);
			splash.getElement().getStyle()
			        .setLeft((width / 2) - (splashWidth / 2), Unit.PX);

		}
		addStyleName("jsloaded");
		add(splash);
	}
	
	private ArticleElement ae;
	
	@Override
    protected void onLoad() {
		runAsyncAfterSplash(this, ae);
	}
	
	protected int dataParamWidth = 0;
	protected int dataParamHeight = 0;
	
	public void setDataParamWidth(int width) {
		this.dataParamWidth = width;
	}

	public void setDataParamHeight(int height) {
		this.dataParamHeight = height;
	}

	public int getDataParamWidth() {
		return dataParamWidth;
	}

	public int getDataParamHeight() {
		return dataParamHeight;
	}

	private static void setBorder(ArticleElement ae, GeoGebraFrame gf, String dpBorder, int px) {
		ae.getStyle().setBorderWidth(0, Style.Unit.PX);
		ae.getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		ae.getStyle().setBorderColor(dpBorder);
		gf.getStyleElement().getStyle().setBorderWidth(px, Style.Unit.PX);
		gf.getStyleElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
		gf.getStyleElement().getStyle().setBorderColor(dpBorder);
	}
	
	/**
	 * Sets the border around the canvas to the data-param-bordercolor property
	 * or leaves it invisible if "none" was set.
	 * @param ae article element
	 * @param gf frame
	 */
	public static void useDataParamBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "#ffffff", 0);
			return;
		}
		
		if (dpBorder == null || dpBorder.length() != 7 ||
			(dpBorder.length() > 0 && dpBorder.charAt(0) != '#')) {
			// FIXME: This check is incomplete, do a complete check.
			dpBorder = "#888888";
		}
		setBorder(ae, gf, dpBorder, 1);
	}

	/**
	 * Sets the border around the canvas to be highlighted. At the moment we use
	 * "#9999ff" for this purpose.
	 * @param ae article element
	 * @param gf frame
	 */
	public static void useFocusedBorder(ArticleElement ae, GeoGebraFrame gf) {
		String dpBorder = ae.getDataParamBorder();
		if (dpBorder != null && dpBorder.equals("none")) {
			setBorder(ae, gf, "#FFFFFF", 0);
			return;
		}
		setBorder(ae, gf, "#9999ff", 1);
	}
	
	public void runAsyncAfterSplash(final GeoGebraFrame inst, final ArticleElement articleElement) {
		GWT.runAsync(new RunAsyncCallback() {
			
			public void onSuccess() {
				ResourcesInjector.injectResources();
				inst.app = inst.createApplication(articleElement, inst);
				inst.setCustomToolBar();
				//useDataParamBorder(articleElement, inst);
			    //inst.add(inst.app.buildApplicationPanel());
				inst.app.buildApplicationPanel();
			    
			}
			
			public void onFailure(Throwable reason) {
				App.debug("Async load failed");
			}
		});
	}

	private static void init(ArrayList<ArticleElement> geoGebraMobileTags) {

		for (final ArticleElement articleElement : geoGebraMobileTags) {
			final GeoGebraFrame inst = new GeoGebraFrame();
			inst.ae = articleElement;
			inst.createSplash(articleElement);	
			RootPanel.get(articleElement.getId()).add(inst);
		}
	}

	private void setCustomToolBar() {
		customToolbar = ae.getDataParamCustomToolBar();
		if ((customToolbar != null) &&
			(customToolbar.length() > 0) &&
			(ae.getDataParamShowToolBar())) {

			app.getGuiManager().setToolBarDefinition(App.VIEW_EUCLIDIAN, customToolbar);
		}
	}

	public static void finishAsyncLoading(ArticleElement articleElement,
            GeoGebraFrame inst, AppW app) {
	    handleLoadFile(articleElement, app);
    }
	
	/**
	 * @param element
	 */
	public static void renderArticleElemnt(final Element element) {
		final ArticleElement article = ArticleElement.as(element);
		Date creationDate = new Date();
		element.setId(GeoGebraConstants.GGM_CLASS_NAME+creationDate.getTime());
		final GeoGebraFrame inst = new GeoGebraFrame();
		inst.ae = article;
		inst.createSplash(article);
		RootPanel.get(article.getId()).add(inst);
	}

	

	private static void handleLoadFile(ArticleElement articleElement,
			AppW app) {
		View view = new View(articleElement, app);
		fileLoader.setView(view);
		fileLoader.onPageLoad();
	}

	/**
	 * @return the application
	 */
	public App getApplication() {
		return app;
	}

	/**
	 * Sets the Application of the GeoGebraFrame
	 * @param app
	 *          the application
	 */
	public void setApplication(AppW app) {
		this.app = app;
	}

	/**
	 * @param useFullGui
	 *          if false only one euclidianView will be available (without
	 *          menus / ...)
	 * @return the newly created instance of Application
	 */
	protected AppW createApplication(ArticleElement ae, GeoGebraFrame gf) {
		return new AppW(ae, gf);
	}

	/**
	 * @return list of instances of GeogebraFrame
	 */
	public static ArrayList<GeoGebraFrame> getInstances() {
		return instances;
	}

	@Override
	public void onBrowserEvent(Event event) {
		if (app == null || !app.getUseFullGui()){
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

	public static int getInstanceCount() {
		return instances.size();
	}
}
